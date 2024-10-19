package br.com.noartcode.theprice.ui.presentation.payment.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetBillByID
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetPaymentByID
import br.com.noartcode.theprice.domain.usecases.IUpdatePayment
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi.Status.*
import br.com.noartcode.theprice.ui.presentation.payment.edit.model.PaymentEditEvent
import br.com.noartcode.theprice.ui.presentation.payment.edit.model.PaymentEditUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentEditViewModel(
    private val getPayment: IGetPaymentByID,
    private val getBill: IGetBillByID,
    private val currencyFormatter: ICurrencyFormatter,
    private val dateFormatter: IGetDateFormat,
    private val epochFormatter: IEpochMillisecondsFormatter,
    private val updatePayment: IUpdatePayment,
    private val paymentUiMapper: UiMapper<Payment, PaymentUi?>,
) : ViewModel() {

    private var payment:Payment? = null
    private var bill:Bill? = null
    private var paymentUi:PaymentUi? = null


    private val _state = MutableStateFlow(PaymentEditUiState())
    val uiState = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaymentEditUiState()
    )

    fun onEvent(event: PaymentEditEvent) = viewModelScope.launch {
        when(event){

            is PaymentEditEvent.OnPaidAtDateChanged -> {
                val originalPaidDate = (payment!!.paidAt ?: payment!!.dueDate).let { date -> epochFormatter.from(date) }
                _state.update {
                    it.copy(
                        paidAtDate = event.date,
                        paidDateTitle = dateFormatter(epochFormatter.to(event.date)),
                        dateHasChanged = event.date != originalPaidDate,
                    )
                }
            }

            is PaymentEditEvent.OnPriceChanged -> {
                val value = currencyFormatter.clenup(event.value)
                val newPrice = currencyFormatter.format(value)
                _state.update {
                    it.copy(
                        payedValue = newPrice,
                        priceHasError = value == 0,
                        priceHasChanged = newPrice != paymentUi!!.price
                    )
                }
            }

            is PaymentEditEvent.OnStatusChanged -> {
                val newStatus = generateNewPaymentStatus(_state.value.paymentStatus)
                _state.update {
                    it.copy(
                        paymentStatus =  newStatus,
                        statusHasChanged = newStatus != paymentUi!!.status
                    )
                }
            }

            PaymentEditEvent.OnSave -> {
                _state.update {
                    it.copy(
                        askingConfirmation = true,
                    )
                }
            }

            is PaymentEditEvent.OnGetPayment -> {
                setupPayment(event.id)
            }


            PaymentEditEvent.OnChangeAllFuturePayments -> {

            }

            PaymentEditEvent.OnChangeOnlyCurrentPayment -> {
                _state.update {
                    it.copy(
                        askingConfirmation = false,
                        isSaving = true
                    )
                }
                updatePayment(
                    id = payment!!.id,
                    payedValue = if(_state.value.paymentStatus == PAYED) _state.value.payedValue else null,
                    paidAt = if(_state.value.paymentStatus == PAYED) epochFormatter.to(_state.value.paidAtDate) else null
                )
                _state.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true
                    )
                }
            }

            PaymentEditEvent.OnDismissConfirmationDialog -> {
                _state.update {
                    it.copy(
                        askingConfirmation = false,
                    )
                }
            }
        }
    }

    private suspend fun generateNewPaymentStatus(status: PaymentUi.Status) : PaymentUi.Status =
        when(status) {
            OVERDUE, PENDING -> PAYED
            PAYED -> {
                val paymentWithoutPaidDate = payment!!.copy(paidAt = null)
                paymentUiMapper.mapFrom(paymentWithoutPaidDate)!!.status
            }
        }


    private suspend fun setupPayment(paymentId:Long) {
        payment = checkNotNull(getPayment(paymentId))
        bill = getBill(payment!!.billId)
        paymentUi = paymentUiMapper.mapFrom(payment!!)
        _state.update {
            it.copy(
                billName = bill!!.name,
                payedValue = paymentUi!!.price,
                paidAtDate = (payment!!.paidAt ?: payment!!.dueDate).let { date -> epochFormatter.from(date) },
                paidDateTitle = dateFormatter(payment!!.dueDate),
                paymentStatus = paymentUi!!.status,
            )
        }
    }
}

/*

        val mockPrice = currencyFormatter.format(0)
        val price = (original?.payedValue ?: b?.price)?.let { currencyFormatter.format(it) } ?: ui?.price ?: mockPrice
        PaymentEditUiState(
            billName = ui?.billName ?: "",
            payedValue = price,
            paidAtDate = (original?.paidAt ?: b?.createAt)?.let { date -> epochFormatter.from(date) }  ?: 0L,
            paidDateTitle = (original?.paidAt ?: b?.createAt)?.let { date -> dateFormatter(date) } ?: "",
            paymentStatus = ui?.status ?: PaymentUi.Status.PENDING,
            priceHasError = price == mockPrice,
            askingConfirmation = confirm
 */