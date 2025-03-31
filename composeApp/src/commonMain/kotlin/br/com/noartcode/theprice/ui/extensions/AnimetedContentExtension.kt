package br.com.noartcode.theprice.ui.extensions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

fun AnimatedContentTransitionScope<NavBackStackEntry>.enterAnimation() : EnterTransition {
    return slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Up,
        tween(500)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.exitAnimation() : ExitTransition {
    return slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Down,
        tween(300)
    )
}