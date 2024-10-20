package br.com.noartcode.theprice.ui.presentation.payment.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.ui.presentation.payment.edit.model.PaymentEditEvent
import br.com.noartcode.theprice.ui.presentation.payment.edit.model.PaymentEditUiState
import br.com.noartcode.theprice.ui.views.BottomCircularButton
import br.com.noartcode.theprice.ui.views.ConfirmPaymentChangeDialog
import br.com.noartcode.theprice.ui.views.DateEditFieldView
import br.com.noartcode.theprice.ui.views.NormalEditField

@Composable
fun PaymentEditScreen(
    modifier: Modifier = Modifier,
    state: PaymentEditUiState,
    onEvent: (event: PaymentEditEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                state.billName,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(20.dp))
            NormalEditField(
                fieldName = if (state.paymentStatus == PaymentUi.Status.PAYED)  "Payed amount" else "Amount to pay",
                fieldLabel = if (state.paymentStatus == PaymentUi.Status.PAYED) "Enter the payed amount" else "Enter the amount to pay",
                value = state.payedValue,
                hasError = state.priceHasError,
                onValueChanged = { onEvent(PaymentEditEvent.OnPriceChanged(it)) },
                keyboardOptions = KeyboardOptions().copy(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(20.dp))
            DateEditFieldView(
                title = if (state.paymentStatus == PaymentUi.Status.PAYED)  "Paid on" else "Due date",
                dateTitle = state.paidDateTitle,
                selectedDate = state.paidAtDate,
                onSelectDate = { date -> onEvent(PaymentEditEvent.OnPaidAtDateChanged(date)) },
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onEvent(PaymentEditEvent.OnStatusChanged) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (state.paymentStatus == PaymentUi.Status.PAYED) Color.Magenta else Color.LightGray
                )
            ) {
                Text(if (state.paymentStatus == PaymentUi.Status.PAYED) "Paid" else "Unpaid")
            }
            BottomCircularButton(
                isEnable = state.canSave,
                icon = Icons.Filled.Check,
                color = Color.Magenta,
                onClick = { onEvent(PaymentEditEvent.OnSave) }
            )
        }
        if (state.askingConfirmation) {
            ConfirmPaymentChangeDialog(
                onDismiss = { onEvent(PaymentEditEvent.OnDismissConfirmationDialog) },
                onConfirmToCurrent = { onEvent(PaymentEditEvent.OnChangeOnlyCurrentPayment) },
                onConfirmToAll = { onEvent(PaymentEditEvent.OnChangeAllFuturePayments) }
            )
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }
    
}


@Composable
fun PaymentEditInfoContainer(
    description:String,
    value:String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            description,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


