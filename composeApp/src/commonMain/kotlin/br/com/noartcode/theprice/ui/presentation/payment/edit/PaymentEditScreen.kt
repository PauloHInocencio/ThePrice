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
import br.com.noartcode.theprice.ui.presentation.payment.edit.model.PaymentEditEvent
import br.com.noartcode.theprice.ui.presentation.payment.edit.model.PaymentEditUiState
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
                fieldName = if (state.paidAtDate != null)  "Payed amount" else "Amount to pay",
                fieldLabel = if (state.paidAtDate != null) "Enter the payed amount" else "Enter the amount to pay",
                value = if (state.paidAtDate != null) state.payedValue!! else state.billOriginalPrice,
                hasError = state.priceHasError,
                onValueChanged = { onEvent(PaymentEditEvent.OnPriceChanged(it)) },
                keyboardOptions = KeyboardOptions().copy(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(10.dp))
            DateEditFieldView(
                title = if (state.paidAtDate != null)  "Paid on" else "Due date",
                dateTitle = state.paidDateTitle ?: state.billDueDateTitle,
                selectedDate = state.paidAtDate ?: state.billDueDate,
                onSelectDate = { date -> onEvent(PaymentEditEvent.OnPaidAtDateChanged(date)) },
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {},
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (state.paidAtDate != null) Color.Magenta else Color.LightGray
                )
            ) {
                Text(if (state.paidAtDate != null) "Paid" else "Unpaid")
            }
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


