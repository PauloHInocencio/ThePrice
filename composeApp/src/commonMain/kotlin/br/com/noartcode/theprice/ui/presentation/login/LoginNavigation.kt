package br.com.noartcode.theprice.ui.presentation.login

import androidx.compose.animation.AnimatedContentTransitionScope.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Serializable
data object LoginDestination

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.composeLoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToNewAccount: () -> Unit,
){
    composable<LoginDestination>(
        enterTransition = { fadeIn() + slideIntoContainer(SlideDirection.Left) },
        exitTransition = { fadeOut() + slideOutOfContainer(SlideDirection.Left) },
        popEnterTransition = { fadeIn() + slideIntoContainer(SlideDirection.Right) },
        popExitTransition = { fadeOut() + slideOutOfContainer(SlideDirection.Right) }
    ) {
        val viewModel = koinViewModel<LoginViewModel>()
        LoginScreen(
            uiState = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
            onNavigateToHome = onNavigateToHome,
            onNavigateToNewAccount = onNavigateToNewAccount,
        )
    }
}

fun NavController.navigateToLogin(){
    navigate(LoginDestination) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}