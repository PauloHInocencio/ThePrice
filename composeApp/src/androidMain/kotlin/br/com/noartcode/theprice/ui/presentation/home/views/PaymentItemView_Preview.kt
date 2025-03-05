package br.com.noartcode.theprice.ui.presentation.home.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.ui.presentation.home.views.PaymentItemView

@Preview
@Composable
fun PaymentItemView_Preview() {
    Column {
        PaymentItemView(
            payment = PaymentUi(
                id = "",
                statusDescription = "Paga em 12/08/2024",
                status = PaymentUi.Status.PAYED,
                billName = "Internet",
                price = "R$ 120,00"
            ),
            onStatusClicked = {},
            onPaymentClicked = {}
        )

        PaymentItemView(
            payment = PaymentUi(
                id = "",
                statusDescription = "Vence em 5 dias",
                status = PaymentUi.Status.PENDING,
                billName = "Luz",
                price = "R$ 320,22"
            ),
            onStatusClicked = {},
            onPaymentClicked = {}
        )

        PaymentItemView(
            payment = PaymentUi(
                id = "",
                statusDescription = "Venceu faz 10 dias",
                status = PaymentUi.Status.OVERDUE,
                billName = "Internet Mãe",
                price = "R$ 99,00"
            ),
            onStatusClicked = {},
            onPaymentClicked = {}
        )
    }

}