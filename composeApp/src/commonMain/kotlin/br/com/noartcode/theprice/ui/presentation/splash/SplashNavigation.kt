package br.com.noartcode.theprice.ui.presentation.splash

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SplashDestination

fun NavGraphBuilder.composeSplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    composable<SplashDestination> {
        val viewModel = koinViewModel<SplashViewModel>()
        SplashScreen(
            uiState = viewModel.uiState.collectAsState().value,
            onNavigateToHome = onNavigateToHome,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}
