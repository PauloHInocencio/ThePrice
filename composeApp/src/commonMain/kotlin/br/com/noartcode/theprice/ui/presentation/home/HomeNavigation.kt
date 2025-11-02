package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object HomeDestination

fun NavGraphBuilder.composeHomeScreen(
    onNavigateToAddBill:() -> Unit,
    onNavigateToEditPayment: (paymentId:String) -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    composable<HomeDestination> {
        val viewModel = koinViewModel<HomeViewModel>()
        HomeScreen(
            state = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
            onNavigateToAddBill = onNavigateToAddBill,
            onNavigateToEditPayment = onNavigateToEditPayment,
            onNavigateToAccount = onNavigateToAccount,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}

fun NavController.navigateToHome(){
    navigate(HomeDestination) {
        popUpTo(0) {
            inclusive = true
        }
    }
}