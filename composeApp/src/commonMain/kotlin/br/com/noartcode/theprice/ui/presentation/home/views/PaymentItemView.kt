package br.com.noartcode.theprice.ui.presentation.home.views


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
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import br.com.noartcode.theprice.ui.theme.OverdueStatus
import br.com.noartcode.theprice.ui.theme.PaidStatus
import br.com.noartcode.theprice.ui.theme.PendingStatus

@Composable
fun PaymentItemView(
    payment: PaymentUi,
    modifier: Modifier = Modifier,
    onStatusClicked: (PaymentUi) -> Unit,
    onPaymentClicked : (String) -> Unit
) {

    ListItem(
        modifier = modifier,
        headlineContent = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable { onPaymentClicked(payment.id) },
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PaymentItemStatusView(
                        description = payment.statusDescription,
                        status = payment.status
                    )
                    Text(
                        payment.billName.capitalizeWords(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = payment.price,
                        fontSize = 16.sp,
                        maxLines = 1,
                        textDecoration = if(payment.status == PaymentUi.Status.PAYED) {
                            TextDecoration.LineThrough
                        } else { TextDecoration.None },
                        fontStyle = if(payment.status == PaymentUi.Status.PAYED) {
                            FontStyle.Italic
                        } else { FontStyle.Normal }
                    )
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


@Composable
fun PaymentItemStatusView(
    description:String,
    status:PaymentUi.Status
) {

    val backgroundColor = when(status) {
        PaymentUi.Status.PAYED -> PaidStatus
        PaymentUi.Status.PENDING -> PendingStatus
        PaymentUi.Status.OVERDUE -> OverdueStatus
    }


    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(vertical = 0.dp, horizontal = 6.dp)
    ) {
        Text(
            text = description,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 0.dp)
        )
    }
}


//Extension Function
fun String.capitalizeWords(delimiter: String = " ") =
    split(delimiter).joinToString(delimiter) { word ->
        val smallCaseWord = word.lowercase()
        smallCaseWord.replaceFirstChar(Char::titlecaseChar)
    }