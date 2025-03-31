package br.com.noartcode.theprice.ui.presentation.account

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Serializable
private data object AccountDestination

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.composeAccountScreen(
    onNavigateToHome:() -> Unit,
    onNavigateToLogin:() -> Unit,
){
   composable<AccountDestination> {
       val viewModel = koinViewModel<AccountViewModel>()
       AccountScreen(
           state = viewModel.uiState.collectAsState().value,
           onEvent = viewModel::onEvent,
           onNavigateToHome = onNavigateToHome,
           onNavigateToLogin = onNavigateToLogin,
       )
   }
}

fun NavController.navigateToAccount() {
    navigate(AccountDestination)
}