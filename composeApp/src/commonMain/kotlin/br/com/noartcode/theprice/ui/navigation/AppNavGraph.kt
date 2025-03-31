package br.com.noartcode.theprice.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import br.com.noartcode.theprice.ui.presentation.account.composeAccountScreen
import br.com.noartcode.theprice.ui.presentation.bill.add.composeAddBillScreen
import br.com.noartcode.theprice.ui.presentation.bill.add.navigateToAddBill
import br.com.noartcode.theprice.ui.presentation.bill.edit.composeEditBillScreen
import br.com.noartcode.theprice.ui.presentation.bill.edit.navigateToEditBillScreen
import br.com.noartcode.theprice.ui.presentation.home.composeHomeScreen
import br.com.noartcode.theprice.ui.presentation.home.navigateToHome
import br.com.noartcode.theprice.ui.presentation.login.LoginDestination
import br.com.noartcode.theprice.ui.presentation.login.composeLoginScreen
import br.com.noartcode.theprice.ui.presentation.login.navigateToLogin
import br.com.noartcode.theprice.ui.presentation.payment.edit.composeEditPaymentScreen
import br.com.noartcode.theprice.ui.presentation.payment.edit.navigateToEditPaymentScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LoginDestination,
        modifier = modifier.fillMaxSize()
    ) {

        composeHomeScreen(
            onNavigateToEditPayment = { id -> navController.navigateToEditPaymentScreen(id) },
            onNavigateToAddBill = { navController.navigateToAddBill() }
        )

        composeAddBillScreen(
            onNavigateBack = { navController.popBackStack() }
        )

        composeEditBillScreen (
            onNavigateToHome = { navController.navigateToHome() }
        )

        composeEditPaymentScreen(
            onNavigateToEditBill = { id -> navController.navigateToEditBillScreen(id) },
            onNavigateBack = { navController.popBackStack() }
        )

        composeAccountScreen(
            onNavigateToHome = { navController.navigateToHome() },
            onNavigateToLogin = { navController.navigateToLogin() },
        )

        composeLoginScreen(
            onNavigateToHome = { navController.navigateToHome() }
        )
    }
}