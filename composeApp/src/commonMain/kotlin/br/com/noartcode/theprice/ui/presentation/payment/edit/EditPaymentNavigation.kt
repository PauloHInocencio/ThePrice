package br.com.noartcode.theprice.ui.presentation.payment.edit

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import br.com.noartcode.theprice.ui.extensions.enterAnimation
import br.com.noartcode.theprice.ui.extensions.exitAnimation
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Serializable
private data class EditPaymentDestination(
    val paymentId:String
)

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.composeEditPaymentScreen(
    onNavigateToEditBill: (billId:String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<EditPaymentDestination>(
        enterTransition = { enterAnimation() },
        popExitTransition = { exitAnimation() },
    ) { entry ->
        val viewModel = koinViewModel<EditPaymentViewModel>()
        EditPaymentScreen(
            state = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
            onNavigateToEditBill = onNavigateToEditBill,
            onNavigateBack = onNavigateBack,
        )
        //TODO("Retrieve the PaymentID inside the ViewModel")
        LaunchedEffect(Unit){
            val id = entry.toRoute<EditPaymentDestination>().paymentId
            viewModel.onEvent(EditPaymentEvent.OnGetPayment(id))
        }
    }
}

fun NavController.navigateToEditPaymentScreen(paymentId: String) {
    navigate(EditPaymentDestination(paymentId))
}