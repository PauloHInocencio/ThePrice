package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Serializable
private data object HomeDestination

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.composeHomeScreen(
    onNavigateToAddBill:() -> Unit,
    onNavigateToEditPayment: (paymentId:String) -> Unit,
) {
    composable<HomeDestination> {
        val viewModel = koinViewModel<HomeViewModel>()
        HomeScreen(
            state = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
            onNavigateToAddBill = onNavigateToAddBill,
            onNavigateToEditPayment = onNavigateToEditPayment,
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