package br.com.noartcode.theprice.ui.presentation.bill.add

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import br.com.noartcode.theprice.ui.extensions.enterAnimation
import br.com.noartcode.theprice.ui.extensions.exitAnimation
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object AddBillDestination

fun NavGraphBuilder.composeAddBillScreen(
    onNavigateBack: () -> Unit,
) {
    composable<AddBillDestination>(
        enterTransition = { enterAnimation() },
        popExitTransition = { exitAnimation() }
    ) {
        val viewModel = koinViewModel<AddBillViewModel>()
        AddBillScreen(
            state = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavController.navigateToAddBill() {
    navigate(AddBillDestination)
}