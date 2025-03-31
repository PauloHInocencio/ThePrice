package br.com.noartcode.theprice.ui.presentation.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun AccountScreen_Preview() {
    AccountScreen(
        modifier = Modifier,
        state = AccountUiState(),
        onEvent = {},
        onNavigateToLogin = {},
        onNavigateToHome = {},
    )
}
