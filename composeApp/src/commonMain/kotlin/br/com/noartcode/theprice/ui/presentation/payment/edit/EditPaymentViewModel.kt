package br.com.noartcode.theprice.ui.presentation.payment.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetBillByID
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetPaymentByID
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IUpdatePayment
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi.Status.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditPaymentViewModel(
    private val getPayment: IGetPaymentByID,
    private val getBill: IGetBillByID,
    private val currencyFormatter: ICurrencyFormatter,
    private val dateFormatter: IGetDateFormat,
    private val epochFormatter: IEpochMillisecondsFormatter,
    private val updatePayment: IUpdatePayment,
    private val paymentUiMapper: UiMapper<Payment, PaymentUi?>,
    private val getTodayDate: IGetTodayDate,
) : ViewModel() {

    private var payment:Payment? = null
    private var bill:Bill? = null
    private var paymentUi: PaymentUi? = null


    private val _state = MutableStateFlow(EditPaymentUiState())
    val uiState = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditPaymentUiState()
    )

    fun onEvent(event: EditPaymentEvent) = viewModelScope.launch {
        when(event){

            is EditPaymentEvent.OnPaidAtDateChanged -> {
                val originalPaidDate = (payment!!.dueDate).let { date -> epochFormatter.from(date) }
                _state.update {
                    it.copy(
                        paidAtDate = event.date,
                        paidDateTitle = dateFormatter(epochFormatter.to(event.date)),
                        dateHasChanged = event.date != originalPaidDate,
                    )
                }
            }

            is EditPaymentEvent.OnPriceChanged -> {
                val value = currencyFormatter.clenup(event.value)
                val newPrice = currencyFormatter.format(value)
                _state.update {
                    it.copy(
                        payedValue = newPrice,
                        priceHasError = value == 0L,
                        priceHasChanged = newPrice != paymentUi!!.price
                    )
                }
            }

            is EditPaymentEvent.OnStatusChanged -> {
                val newStatus = generateNewPaymentStatus(_state.value.paymentStatus)
                _state.update {
                    it.copy(
                        paymentStatus =  newStatus,
                        statusHasChanged = newStatus != paymentUi!!.status
                    )
                }
            }

            EditPaymentEvent.OnSave -> {
                _state.update {
                    it.copy(
                        askingConfirmation = true,
                    )
                }
            }

            is EditPaymentEvent.OnGetPayment -> {
                setupPayment(event.id)
            }


            EditPaymentEvent.OnChangeAllFuturePayments -> {

            }

            EditPaymentEvent.OnChangeOnlyCurrentPayment -> {
                _state.update {
                    it.copy(
                        askingConfirmation = false,
                        isSaving = true
                    )
                }
                updatePayment(
                    Payment(
                        id = payment!!.id,
                        billId = payment!!.billId,
                        price = currencyFormatter.clenup(_state.value.payedValue),
                        dueDate =  epochFormatter.to(_state.value.paidAtDate),
                        isPayed = _state.value.paymentStatus == PAYED,
                        createdAt = payment!!.createdAt,
                        updatedAt = getTodayDate().toEpochMilliseconds(),
                        isSynced = false,
                    )

                )
                _state.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true
                    )
                }
            }

            EditPaymentEvent.OnDismissConfirmationDialog -> {
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
                val paymentWithoutPaidDate = payment!!.copy(isPayed = false)
                paymentUiMapper.mapFrom(paymentWithoutPaidDate)!!.status
            }
        }


    private suspend fun setupPayment(paymentId:String) {
        payment = checkNotNull(getPayment(paymentId))
        bill = getBill(payment!!.billId)
        paymentUi = paymentUiMapper.mapFrom(payment!!)
        _state.update {
            it.copy(
                billId = bill!!.id,
                billName = bill!!.name,
                payedValue = paymentUi!!.price,
                paidAtDate = payment!!.dueDate.let { date -> epochFormatter.from(date) },
                paidDateTitle = dateFormatter(payment!!.dueDate),
                paymentStatus = paymentUi!!.status,
            )
        }
    }
}