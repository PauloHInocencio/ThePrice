package br.com.noartcode.theprice.ui.presentation.payment.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.data.local.mapper.toSyncEvent
import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.bill.IGetBillByID
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBillAndApplyToPayments
import br.com.noartcode.theprice.domain.usecases.payment.IGetPaymentByID
import br.com.noartcode.theprice.domain.usecases.payment.IUpdatePayment
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi.Status.*
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
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
    private val updateBillAndApplyToPayments: IUpdateBillAndApplyToPayments,
    private val eventSyncQueue: EventSyncQueue,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private lateinit var payment:Payment
    private lateinit var bill:Bill
    private var paymentUi: PaymentUi? = null


    private val _uiState = MutableStateFlow(EditPaymentUiState())
    val uiState = _uiState
        .onStart { setupPayment(savedStateHandle["paymentId"]) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EditPaymentUiState()
        )

    fun onEvent(event: EditPaymentEvent) = viewModelScope.launch {
        when(event){

            is EditPaymentEvent.OnPaidAtDateChanged -> {
                val originalPaidDate = (payment.dueDate).let { date -> epochFormatter.from(date) }
                _uiState.update {
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
                _uiState.update {
                    it.copy(
                        payedValue = newPrice,
                        priceHasError = value == 0L,
                        priceHasChanged = newPrice != paymentUi!!.price
                    )
                }
            }

            is EditPaymentEvent.OnStatusChanged -> {
                val newStatus = generateNewPaymentStatus(_uiState.value.paymentStatus)
                _uiState.update {
                    it.copy(
                        paymentStatus =  newStatus,
                        statusHasChanged = newStatus != paymentUi!!.status
                    )
                }
            }

            EditPaymentEvent.OnSave -> {
                if (_uiState.value.priceHasChanged) {
                    _uiState.update { it.copy(askingConfirmation = true) }
                } else {
                    _uiState.update { it.copy(isSaving = true) }
                    changeOnlyCurrentPayment()
                }
            }


            EditPaymentEvent.OnChangeAllFuturePayments -> {
                _uiState.update { it.copy(askingConfirmation = false, isSaving = true) }
                applyChangesToFuturePayments(from = payment.dueDate)
            }

            EditPaymentEvent.OnChangeOnlyCurrentPayment -> {
                _uiState.update { it.copy(askingConfirmation = false, isSaving = true) }
               changeOnlyCurrentPayment()
            }

            EditPaymentEvent.OnDismissConfirmationDialog -> {
                _uiState.update { it.copy(askingConfirmation = false) }
            }
        }
    }

    private suspend fun changeOnlyCurrentPayment() {
       /* val p = Payment(
            id = payment.id,
            billId = payment.billId,
            price = currencyFormatter.clenup(_state.value.payedValue),
            dueDate =  epochFormatter.to(_state.value.paidAtDate),
            isPayed = _state.value.paymentStatus == PAYED,
            createdAt = payment.createdAt,
            updatedAt = getTodayDate().toEpochMilliseconds(),
            isSynced = false,
        )*/
        val paymentToUpdate = payment.copy(
            price = currencyFormatter.clenup(_uiState.value.payedValue),
            isPayed = _uiState.value.paymentStatus == PAYED
        )
        updatePayment(paymentToUpdate).doIfSuccess { updatedPayment ->
            eventSyncQueue.enqueue(updatedPayment.toSyncEvent("update"))
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }.doIfError { error ->
            _uiState.update { it.copy(errorMessage = error.message, isSaving = false) }
        }
    }

    private suspend fun applyChangesToFuturePayments(from: DayMonthAndYear?) {
        val priceInt = currencyFormatter.clenup(_uiState.value.payedValue)
        val changedBill = bill.copy(
            price = priceInt // the only bill info that this screen can change
        )

        // If status has changed, we should update the payment first
        if (_uiState.value.statusHasChanged) {
            val paymentToUpdate = payment.copy(
                price = priceInt,
                isPayed = _uiState.value.paymentStatus == PAYED
            )
            updatePayment(paymentToUpdate).doIfSuccess { updatedPayment ->
                eventSyncQueue.enqueue(updatedPayment.toSyncEvent("update"))
            }
        }

        updateBillAndApplyToPayments(changedBill, from)
            .doIfSuccess { updatedBillWithPayments ->
                eventSyncQueue.enqueue(updatedBillWithPayments.toSyncEvent("update"))
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            }
            .doIfError { error ->
                _uiState.update { it.copy(errorMessage = error.message, isSaving = false) }
            }
    }

    private suspend fun generateNewPaymentStatus(status: PaymentUi.Status) : PaymentUi.Status =
        when(status) {
            OVERDUE, PENDING -> PAYED
            PAYED -> {
                val paymentWithoutPaidDate = payment.copy(isPayed = false)
                paymentUiMapper.mapFrom(paymentWithoutPaidDate)!!.status
            }
        }


    private suspend fun setupPayment(paymentId:String?) {
        if (paymentId != null) {
            try {
                payment = getPayment(paymentId)!!
                bill = getBill(payment.billId)!!
                paymentUi = paymentUiMapper.mapFrom(payment)
                _uiState.update {
                    it.copy(
                        billId = bill.id,
                        billName = bill.name,
                        payedValue = paymentUi!!.price,
                        paidAtDate = payment.dueDate.let { date -> epochFormatter.from(date) },
                        paidDateTitle = dateFormatter(payment.dueDate),
                        paymentStatus = paymentUi!!.status,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage ="Setup failed due to ${e.message}")
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    errorMessage = "paymentId is null"
                )
            }
        }
    }
}