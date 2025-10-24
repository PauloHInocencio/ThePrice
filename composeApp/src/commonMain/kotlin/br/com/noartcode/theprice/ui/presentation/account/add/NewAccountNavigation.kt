package br.com.noartcode.theprice.ui.presentation.account.add

import androidx.compose.animation.AnimatedContentTransitionScope.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object NewAccountDestination

fun NavGraphBuilder.composeNewAccountScreen(
    onNavigateToLogin:() -> Unit,
    onNavigateToHome:() -> Unit,
) {
    composable<NewAccountDestination>(
        enterTransition = { fadeIn() + slideIntoContainer(SlideDirection.Left) },
        exitTransition = { fadeOut() + slideOutOfContainer(SlideDirection.Left) },
        popEnterTransition = { fadeIn() + slideIntoContainer(SlideDirection.Right) },
        popExitTransition = { fadeOut() + slideOutOfContainer(SlideDirection.Right) }
    ) {
        val viewModel = koinViewModel<NewAccountViewModel>()
        NewAccountScreen(
            uiState = viewModel.uiState.collectAsState().value,
            onEvent = viewModel::onEvent,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToHome = onNavigateToHome
        )
    }
}

fun NavController.navigateToNewAccount() {
    navigate(NewAccountDestination)
}