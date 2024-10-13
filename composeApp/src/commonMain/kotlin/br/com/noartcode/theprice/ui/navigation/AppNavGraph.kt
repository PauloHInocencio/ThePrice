package br.com.noartcode.theprice.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.noartcode.theprice.ui.presentation.home.HomeScreen
import br.com.noartcode.theprice.ui.presentation.home.HomeViewModel
import br.com.noartcode.theprice.ui.presentation.newbill.NewBillScreen
import br.com.noartcode.theprice.ui.presentation.newbill.NewBillViewModel
import br.com.noartcode.theprice.ui.presentation.payment.edit.PaymentEditScreen
import br.com.noartcode.theprice.ui.presentation.payment.edit.PaymentEditViewModel
import br.com.noartcode.theprice.ui.presentation.payment.edit.model.PaymentEditEvent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {

    val backStackEntry by navController.currentBackStackEntryAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME.name,
        modifier = modifier.fillMaxSize()
    ) {
        composable(
            route = Routes.HOME.name
        ) {
            val viewModel = koinViewModel<HomeViewModel>()
            HomeScreen(
                state = viewModel.uiState.collectAsState().value,
                onEvent = viewModel::onEvent,
                onNavigateToNewBill = {
                    navController.navigate(Routes.NEW_BILL.name)
                },
                onNavigateToEditPayment = { paymentId ->
                    navController.navigate("${Routes.EDIT_PAYMENT.name}?paymentId=$paymentId")
                }
            )
        }

        composable(
            route = Routes.NEW_BILL.name,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(500)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(300)
                )
            }
        ) {
            val viewModel = koinViewModel<NewBillViewModel>()
            NewBillScreen(
                state = viewModel.uiState.collectAsState().value,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Routes.EDIT_PAYMENT.name}?paymentId={paymentId}",
            arguments = listOf(navArgument("paymentId"){ type = NavType.LongType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(500)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(300)
                )
            }
        ){
            val viewModel = koinViewModel<PaymentEditViewModel>()
            PaymentEditScreen(
                state = viewModel.uiState.collectAsState().value,
                onEvent = viewModel::onEvent,
                onNavigateBack = {}
            )
            LaunchedEffect(Unit){
                val paymentId = checkNotNull(backStackEntry?.arguments?.getLong("paymentId"))
                viewModel.onEvent(PaymentEditEvent.OnGetPayment(paymentId))
            }
        }
    }

}