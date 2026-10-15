package br.com.noartcode.theprice.ui.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.pric_logo

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(uiState) {
        if (uiState.isCheckingSession) return@LaunchedEffect
        if (uiState.isAuthenticated) onNavigateToHome() else onNavigateToLogin()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(Res.drawable.pric_logo),
            contentDescription = null,
        )
        Text(
            text = "ThePrice",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(red = 69, green = 154, blue = 229),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview
@Composable
private fun SplashScreen_Preview() {
    SplashScreen(
        uiState = SplashUiState(),
        onNavigateToHome = { },
        onNavigateToLogin = { },
    )
}
