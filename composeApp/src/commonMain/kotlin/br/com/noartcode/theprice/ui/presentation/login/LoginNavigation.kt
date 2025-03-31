package br.com.noartcode.theprice.ui.presentation.login

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import br.com.noartcode.theprice.ui.extensions.enterAnimation
import br.com.noartcode.theprice.ui.extensions.exitAnimation
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Serializable
data object LoginDestination

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.composeLoginScreen(
    onNavigateToHome: () -> Unit,
){
    composable<LoginDestination>(
        enterTransition = { enterAnimation() },
        popExitTransition = { exitAnimation() },
    ) {
        val viewModel = koinViewModel<LoginViewModel>()
        LoginScreen(
            uiState = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
            onNavigateToHome = onNavigateToHome
        )
    }
}

fun NavController.navigateToLogin(){
    navigate(LoginDestination)
}