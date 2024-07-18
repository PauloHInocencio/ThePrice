package br.com.noartcode.theprice.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.noartcode.theprice.ui.presentation.home.PaymentsScreen
import br.com.noartcode.theprice.ui.presentation.home.PaymentsViewModel
import br.com.noartcode.theprice.ui.presentation.newBill.NewBillScreen
import br.com.noartcode.theprice.ui.presentation.newBill.NewBillViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME.name,
        modifier = modifier.fillMaxSize()
    ) {
        composable(route = Routes.HOME.name) {
            val viewModel = koinViewModel<PaymentsViewModel>()
            PaymentsScreen()
        }

        composable(route = Routes.NEW_BILL.name) {
            val viewModel = koinViewModel<NewBillViewModel>()
            NewBillScreen()
        }
    }

}