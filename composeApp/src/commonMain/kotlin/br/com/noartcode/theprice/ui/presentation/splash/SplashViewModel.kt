package br.com.noartcode.theprice.ui.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.domain.usecases.settings.ILoadAppSettings
import br.com.noartcode.theprice.domain.usecases.user.IGetUserAccountInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SplashViewModel(
    private val getAccountInfo: IGetUserAccountInfo,
    private val overdueReminderWorker: IOverduePaymentReminderWorker,
    private val loadAppSettings: ILoadAppSettings,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState
        .onStart { checkSession() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SplashUiState()
        )

    private suspend fun checkSession() {
        // Decided only once. A resubscription (onStart runs again) or credentials saved later
        // during a login must not change the decision; the login flow navigates by itself.
        if (!_uiState.value.isCheckingSession) return

        // Resolves language/currency for this process before any localized screen is shown
        loadAppSettings()

        val user = getAccountInfo().first()
        if (user != null) {
            // Keep an existing schedule; only creates it if missing (e.g. a past login was cut short)
            overdueReminderWorker.sweepDailyDue(restart = false)
        }
        _uiState.update { it.copy(isCheckingSession = false, isAuthenticated = user != null) }
    }
}
