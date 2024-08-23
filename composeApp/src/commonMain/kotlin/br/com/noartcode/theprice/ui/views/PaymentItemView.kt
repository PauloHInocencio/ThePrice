package br.com.noartcode.theprice.ui.views


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi

@Composable
fun PaymentItemView(
    payment: PaymentUi,
    modifier: Modifier = Modifier,
    onStatusClicked: (PaymentUi) -> Unit,
    onPaymentClicked : (Long) -> Unit
) {

    ListItem(
        modifier = modifier,
        headlineContent = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clickable { onPaymentClicked(payment.id) }
            ) {
                Column {
                    Text(payment.statusDescription)
                    Text(payment.billName.capitalizeWords())
                    Text(payment.price)
                }
            }
        },
        trailingContent = {
            CheckButtonView(
                isChecked = payment.status == PaymentUi.Status.PAYED,
                onClick = { onStatusClicked(payment) }
            )
        }
    )
}


//Extension Function
fun String.capitalizeWords(delimiter: String = " ") =
    split(delimiter).joinToString(delimiter) { word ->
        val smallCaseWord = word.lowercase()
        smallCaseWord.replaceFirstChar(Char::titlecaseChar)
    }