package br.com.noartcode.theprice.ui.presentation.payment.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetBillByID
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetPaymentByID
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
) : ViewModel() {

    private val state = MutableStateFlow(PaymentEditUiState())
    val uiState = state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaymentEditUiState()
    )

    fun onEvent(event: PaymentEditEvent) {
        when(event){
            is PaymentEditEvent.OnPaidAtDateChanged -> TODO()
            is PaymentEditEvent.OnPriceChanged -> TODO()
            PaymentEditEvent.OnSave -> TODO()
            is PaymentEditEvent.OnGetPayment -> {
                viewModelScope.launch {
                    val payment = checkNotNull(getPayment(event.id))
                    val bill = checkNotNull(getBill(payment.billId))
                    state.update {
                        it.copy(
                            billName = bill.name,
                            billOriginalPrice = currencyFormatter.format(bill.price),
                            billDueDate = epochFormatter.from(bill.createAt),
                            billDueDateTitle = dateFormatter(bill.createAt),
                            payedValue = payment.payedValue?.let { value -> currencyFormatter.format(value) },
                            paidAtDate = payment.paidAt?.let { date -> epochFormatter.from(date) },
                            paidDateTitle = payment.paidAt?.let { date -> dateFormatter(date) },
                        )
                    }
                }
            }
        }
    }

    /*private fun overdueTitle(date: DayMonthAndYear) : String {
        val day = if (date.day < 10) "0${date.day}" else date.day.toString()
        return "Vence dia $day"
    }*/

}