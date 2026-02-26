package br.com.noartcode.theprice.ui.presentation.account

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object AccountDestination

fun NavGraphBuilder.composeAccountScreen(
   onNavigateBack: () -> Unit,
   onNavigateToLogin: () -> Unit,
){
   composable<AccountDestination> {
       val viewModel = koinViewModel<AccountViewModel>()
       AccountScreen(
           state = viewModel.uiState.collectAsState().value,
           onEvent = viewModel::onEvent,
           onNavigateBack = onNavigateBack,
           onNavigateToLogin = onNavigateToLogin,
       )
   }
}

fun NavController.navigateToAccount() {
    navigate(AccountDestination)
}