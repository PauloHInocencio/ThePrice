package br.com.noartcode.theprice.ui.presentation.bill.edit

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
private data class EditBillDestination(
    val billId:String
)

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.composeEditBillScreen(
    onNavigateToHome:() -> Unit,
) {
    composable<EditBillDestination>(
        enterTransition = { enterAnimation() },
        popExitTransition = { exitAnimation() },
    ) { entry ->
        val viewModel = koinViewModel<EditBillViewModel>()
        EditBillScreen(
            state = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
            onNavigateHome = onNavigateToHome,
        )
        //TODO("Retrieve the BillID inside the ViewModel")
        LaunchedEffect(Unit){
            val billId = entry.toRoute<EditBillDestination>().billId
            viewModel.onEvent(EditBillEvent.OnGetBill(billId))
        }
    }

}

fun NavController.navigateToEditBillScreen(billId:String) {
    navigate(EditBillDestination(billId))
}