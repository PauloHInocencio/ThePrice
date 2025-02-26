package br.com.noartcode.theprice.ui.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.IGetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IMoveMonth
import br.com.noartcode.theprice.domain.usecases.IUpdatePaymentStatus
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.presentation.home.model.HomeEvent
import br.com.noartcode.theprice.ui.presentation.home.model.HomeUiState
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi.Status.PAYED
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getPayments: IGetPayments,
    private val getTodayDay: IGetTodayDate,
    private val getMonthName: IGetMonthName,
    private val paymentUiMapper: UiMapper<Payment, PaymentUi?>,
    private val moveMonth: IMoveMonth,
    getFirstPaymentDate: IGetOldestPaymentRecordDate,
    private val updatePaymentStatus: IUpdatePaymentStatus,
): ViewModel() {
    private val initialDate by lazy { getTodayDay() }
    private val _currentDate = MutableStateFlow(initialDate)
    private val _homeScreenResults =
        combine(_currentDate, getFirstPaymentDate()) { currentDate, firstPaymentDate ->
            currentDate to firstPaymentDate
        }
        .flatMapLatest { (currentDate, firstPaymentDate) ->
            getPayments(
                date = currentDate,
                billStatus = Bill.Status.ACTIVE
            ).map { result ->
                result to (currentDate to firstPaymentDate)
            }
        }


    private val _state = MutableStateFlow(HomeUiState())
    val uiState = combine( _state, _homeScreenResults ) { state, (payments, dates) ->
        val (currentDate, firstPaymentDate) = dates
        HomeUiState(
            monthName = getMonthName(currentDate.month)?.plus(" - ${currentDate.year}") ?: "",
            canGoBack = firstPaymentDate != null && moveMonth(by = -1, currentDate = currentDate).let { it  > firstPaymentDate || (it.month == firstPaymentDate.month && it.year == firstPaymentDate.year) },
            canGoNext = currentDate < initialDate,
            payments = (payments as? Resource.Success)?.data?.mapNotNull { payment -> paymentUiMapper.mapFrom(payment) }?.sortedBy { paymentUi -> paymentUi.status } ?: emptyList(),
            errorMessage = (payments as? Resource.Error)?.message ?: state.errorMessage,
            loading = (payments is Resource.Loading),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onEvent(event:HomeEvent) = viewModelScope.launch{
        when(event) {
            HomeEvent.OnBackToPreviousMonth -> {
                _currentDate.update {
                    moveMonth(by = -1, currentDate = it)
                }
            }

            HomeEvent.OnGoToCurrentMonth -> {
                _currentDate.update {
                    initialDate
                }
            }

            HomeEvent.OnGoToNexMonth -> {
                _currentDate.update {
                    moveMonth(by = 1, currentDate = it)
                }
            }

            is HomeEvent.OnPaymentStatusClicked -> {
                updatePaymentStatus(
                    id = event.id,
                    isPayed = event.status != PAYED
                ).doIfError { error ->
                    _state.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }

}