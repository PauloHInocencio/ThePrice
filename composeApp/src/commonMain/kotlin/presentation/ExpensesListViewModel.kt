

package presentation

import domain.repository.ExpensesRepository
import androidx.lifecycle.ViewModel
import domain.model.Expense
import domain.model.ExpenseEntry
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

    data class OnExpenseEntryClicked(val entry: ExpenseEntry) : ExpenseListEvent()

    data object OnNextDayClicked : ExpenseListEvent()

    data object OnPreviousDayClicked : ExpenseListEvent()
}


@OptIn(ExperimentalCoroutinesApi::class)
class ExpensesListViewModel(
    repository: ExpensesRepository
) : ViewModel() {

    private data class InternalState(
        val lastModifiedEntry: ExpenseEntry? = null,
        val date:String = ""
    )

    private val _internalState = MutableStateFlow(InternalState())
    private val _isLoading = MutableStateFlow(false)
    private val _entries = _internalState.flatMapLatest { internalState ->
        repository
            .getAllEntriesByMonth(
                month = ,
                year =
            )
    }



}