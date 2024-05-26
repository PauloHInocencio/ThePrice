

package presentation

import domain.repository.ExpensesRepository
import androidx.lifecycle.ViewModel
import domain.model.Expense
import domain.model.Payment
import domain.usecases.IGetDateMonthAndYear
import domain.usecases.IGetTodayDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

data class ExpenseListUiState(
    val expenses: List<Expense> = listOf(),
    val date:String,
    val dataTitle:String,
    val loading:Boolean = false,
    val canGoBack:Boolean = false
)

sealed class ExpenseListEvent {

    data class OnExpenseEntryClicked(val entry: Payment) : ExpenseListEvent()

    data object OnNextDayClicked : ExpenseListEvent()

    data object OnPreviousDayClicked : ExpenseListEvent()
}


@OptIn(ExperimentalCoroutinesApi::class)
class ExpensesListViewModel(
    private val repository: ExpensesRepository,
    private val getDateMonthAndYear: IGetDateMonthAndYear,
    private val getTodayDate: IGetTodayDate
) : ViewModel() {

    private data class InternalState(
        val lastModifiedEntry: Payment? = null,
        val date:String
    )

    private val _internalState = MutableStateFlow(InternalState(date = getTodayDate()))
    private val _isLoading = MutableStateFlow(false)
    private val _entries = _internalState.flatMapLatest { internalState ->
        val date = getDateMonthAndYear(internalState.date)
        repository
            .getAllPaymentsByMonth(
                month = date.month,
                year = date.year
            )
    }
}