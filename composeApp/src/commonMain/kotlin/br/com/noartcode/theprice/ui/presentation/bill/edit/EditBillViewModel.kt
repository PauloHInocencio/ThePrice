package br.com.noartcode.theprice.ui.presentation.bill.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.data.local.mapper.toSyncEvent
import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.bill.IDeleteLocalBill
import br.com.noartcode.theprice.domain.usecases.bill.IGetBillByID
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBill
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBillAndApplyToPayments
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBillWithPayments
import br.com.noartcode.theprice.ui.presentation.home.views.capitalizeWords
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditBillViewModel(
    private val currencyFormatter: ICurrencyFormatter,
    private val updateBill: IUpdateBill,
    private val getTodayDate: IGetTodayDate,
    private val getMonthName: IGetMonthName,
    private val getBill: IGetBillByID,
    private val deleteBill: IDeleteLocalBill,
    private val eventSyncQueue: EventSyncQueue,
    private val updateBillAndApplyToPayments: IUpdateBillAndApplyToPayments,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private lateinit var originalBill:Bill
    private val _uiState = MutableStateFlow(EditBillUiState())
    val uiState = _uiState
        .onStart { loadOriginalBill(savedStateHandle.get<String>("billId")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EditBillUiState()
        )

    fun onEvent(event: EditBillEvent) = viewModelScope.launch {
        when(event) {
            is EditBillEvent.OnDescriptionChanged -> {
                _uiState.update {
                    it.copy(description = event.description)
                }
            }
            is EditBillEvent.OnNameChanged -> {
                _uiState.update { it.copy(name = event.name) }
            }
            is EditBillEvent.OnPriceChanged -> {
                val priceInt = currencyFormatter.clenup(event.value)
                val newPrice = currencyFormatter.format(priceInt)
                _uiState.update {
                    it.copy(
                        price = newPrice,
                        priceHasError = newPrice == currencyFormatter.format(0)
                    )
                }
            }
            EditBillEvent.OnSave -> {
                val priceInt = currencyFormatter.clenup(_uiState.value.price)
                if (
                    originalBill.billingStartDate != _uiState.value.billingStartDate ||
                    originalBill.price != priceInt) {
                    _uiState.update {
                        it.copy(
                            showingChangePropagationDialog = true,
                        )
                    }
                    return@launch
                }

                // check if something changed
                if (
                    originalBill.name != _uiState.value.name ||
                    originalBill.description !=_uiState.value.description
                ) {
                    _uiState.update { it.copy(isSaving = true) }
                    //updateBillOnly()
                    applyChangesToPayments(from = null)

                } else {
                    println("Nothing to save")
                    _uiState.update { it.copy(canClose = true) }
                }
            }

            is EditBillEvent.OnBillingStartDateChanged -> {
                _uiState.update {
                    it.copy(
                        billingStartDate = event.date,
                    )
                }
            }

            EditBillEvent.OnDeleteBill -> {
               _uiState.update {
                   it.copy(showingConfirmationDialog = true)
               }
            }

            EditBillEvent.OnDeleteBillConfirmed -> {
                _uiState.update {
                    it.copy(
                        showingConfirmationDialog = false,
                        isSaving = true
                    )
                }
                deleteBill(originalBill.id)
                eventSyncQueue.enqueue(originalBill.toSyncEvent("delete"))
                _uiState.update {
                    it.copy(
                        canClose = true
                    )
                }
            }

            EditBillEvent.OnDismissConfirmationDialog -> {
                _uiState.update {
                    it.copy(
                        showingConfirmationDialog = false,
                    )
                }
            }

            EditBillEvent.OnDismissErrorMessage -> {
                _uiState.update {
                    it.copy(
                        errorMessage = null
                    )
                }
            }

            EditBillEvent.OnDismissChangePropagationDialog -> {
                _uiState.update {
                    it.copy(
                        showingChangePropagationDialog = false,
                        isSaving = false,
                    )
                }
            }

            EditBillEvent.OnApplyChangesToAllPayments -> {
                _uiState.update {
                    it.copy(
                        showingChangePropagationDialog = false,
                        isSaving = true,
                    )
                }
                applyChangesToPayments(from = originalBill.billingStartDate)
            }

            EditBillEvent.OnApplyChangesToFuturePayments -> {
                _uiState.update {
                    it.copy(
                        showingChangePropagationDialog = false,
                        isSaving = true,
                    )
                }
                applyChangesToPayments(from = getTodayDate())
            }
        }
    }

    private suspend fun updateBillOnly() {
        val changedBill = originalBill.copy(
            description = _uiState.value.description,
            billingStartDate = _uiState.value.billingStartDate
        )

        updateBill(changedBill)
            .doIfSuccess { updatedBill ->
                eventSyncQueue.enqueue(updatedBill.toSyncEvent("update"))
                _uiState.update { it.copy(canClose = true) }
            }
            .doIfError { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
                println("${error.message}, ${error.exception.toString()}" )
            }
    }

    private suspend fun applyChangesToPayments(from: DayMonthAndYear?) {
        val priceInt = currencyFormatter.clenup(_uiState.value.price)
        val changedBill = originalBill.copy(
            name = _uiState.value.name,
            price = priceInt,
            description = _uiState.value.description,
            billingStartDate = _uiState.value.billingStartDate
        )


        updateBillAndApplyToPayments(changedBill, from)
            .doIfSuccess { updatedBillWithPayments ->
                eventSyncQueue.enqueue(updatedBillWithPayments.toSyncEvent("update"))
                _uiState.update { it.copy(canClose = true) }
            }
            .doIfError { error ->
                _uiState.update { it.copy(errorMessage = error.message, isSaving = false) }
            }
    }

    private fun loadOriginalBill(billID: String?) {
       viewModelScope.launch {
          if (billID != null) {
              originalBill = getBill(billID)!!
              val price = currencyFormatter.format(originalBill.price)
              _uiState.update {
                  it.copy(
                      price = price,
                      name = originalBill.name,
                      description = originalBill.description,
                      billingStartDate = originalBill.billingStartDate,
                      priceHasError = price == currencyFormatter.format(0),
                  )
              }
          } else {
              _uiState.update {
                  it.copy(
                      errorMessage = "Not able to retrieve billID"
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