package br.com.noartcode.theprice.ui.presentation.payment.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun PaymentEditScreen_Preview() {
    PaymentEditScreen(
        state = PaymentEditUiState(
            billName = "Internet",
            payedValue = "R$ 110,00",
            paidAtDate = 1727168591813,
        ),
        onEvent = {},
        onNavigateToEditBill = {},
        onNavigateBack = {},
    )
}
