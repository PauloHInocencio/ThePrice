package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun HomeScreen_Preview() {
    MaterialTheme {
        HomeScreen(
            state = HomeUiState(
                monthName = "Sep 2024",
                payments = listOf(
                    PaymentUi(
                        id = "",
                        statusDescription = "Paga em 12/08/2024",
                        status = PaymentUi.Status.PAYED,
                        billName = "Internet",
                        price = "R$ 120,00"
                    ),
                    PaymentUi(
                        id = "",
                        statusDescription = "Vence em 5 dias",
                        status = PaymentUi.Status.PENDING,
                        billName = "Luz",
                        price = "R$ 320,22"
                    ),
                    PaymentUi(
                        id = "",
                        statusDescription = "Venceu faz 10 dias",
                        status = PaymentUi.Status.OVERDUE,
                        billName = "Internet Mãe",
                        price = "R$ 99,00"
                    ),
                )
            ) ,
            onEvent = { },
            onNavigateToNewBill = {},
            onNavigateToEditPayment = {},
        )
    }

}