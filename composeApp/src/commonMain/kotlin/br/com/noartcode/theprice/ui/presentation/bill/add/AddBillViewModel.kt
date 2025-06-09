package br.com.noartcode.theprice.ui.presentation.bill.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.data.remote.workers.ISyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncPaymentsWorker
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IInsertBill
import br.com.noartcode.theprice.domain.usecases.IInsertBillWithPayments
import br.com.noartcode.theprice.ui.presentation.home.views.capitalizeWords
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddBillViewModel(
    private val currencyFormatter: ICurrencyFormatter,
    private val insertNewBill: IInsertBill,
    private val insertBillWithPayments: IInsertBillWithPayments,
    private val getTodayDate: IGetTodayDate,
    private val epochFormatter: IEpochMillisecondsFormatter,
    private val getMonthName: IGetMonthName,
    private val syncBillWorker: ISyncBillWorker,
    private val syncPayments: ISyncPaymentsWorker,
) : ViewModel() {

    private val bill = MutableStateFlow(
        Bill(
            billingStartDate = getTodayDate(),
            createdAt = getTodayDate().toEpochMilliseconds(),
            updatedAt = getTodayDate().toEpochMilliseconds(),
            isSynced = false,
        )
    )
    private val state = MutableStateFlow(AddBillUiState())
    val uiState: StateFlow<AddBillUiState> = combine(state, bill) {
        s, b ->
        val price = currencyFormatter.format(b.price)
        AddBillUiState(
            price = price,
            name = b.name,
            description = b.description,
            isSaved = b.id.isNotEmpty(),
            isSaving = s.isSaving,
            errorMessage = s.errorMessage,
            billingStartDateTitle = formatTitle(b.billingStartDate),
            billingStartDate = epochFormatter.from(b.billingStartDate),
            priceHasError = price == currencyFormatter.format(0)
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AddBillUiState()
        )

    fun onEvent(event: AddBillEvent) {
        when(event) {
            is AddBillEvent.OnDescriptionChanged -> {
                bill.update { it.copy(description = event.description) }
            }
            is AddBillEvent.OnNameChanged -> {
                bill.update { it.copy(name = event.name) }
            }
            is AddBillEvent.OnPriceChanged -> {
                bill.update { it.copy(price = currencyFormatter.clenup(event.value)) }
            }
            AddBillEvent.OnSave -> {
                viewModelScope.launch {
                    state.update { it.copy(isSaving = true) }
/*                    insertNewBill(bill.value)
                        .doIfSuccess { id ->
                            syncBillWorker.sync(id)
                            bill.update { it.copy(id = id) }
                        }
                        .doIfError { error->
                            state.update { it.copy(errorMessage = error.message) }
                            println("${error.message}, ${error.exception.toString()}" )
                        }*/
                    insertBillWithPayments(bill = bill.value, currentDate = getTodayDate())
                        .doIfSuccess { id ->
                            syncBillWorker.sync(id)
                            syncPayments.sync()
                            bill.update { it.copy(id = id) }
                        }
                        .doIfError { error->
                            state.update { it.copy(errorMessage = error.message) }
                            println("${error.message}, ${error.exception.toString()}" )
                        }
                    state.update { it.copy(isSaving = false) }
                }
            }

            is AddBillEvent.OnBillingStartDateChanged -> {
                bill.update {
                    it.copy(
                        billingStartDate = epochFormatter.to(event.date),
                    )
                }
            }
        }
    }

    private fun formatTitle(date:DayMonthAndYear) : String {
        val monthName =
            getMonthName(date.month)
                ?.take(3)
                ?.capitalizeWords()
                ?.plus(" ${date.year}") ?: return ""
        val day = if (date.day < 10) "0${date.day}" else date.day.toString()
        return "$day $monthName"
    }
}