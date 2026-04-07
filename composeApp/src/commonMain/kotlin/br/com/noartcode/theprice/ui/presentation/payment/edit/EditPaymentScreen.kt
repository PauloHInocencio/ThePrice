package br.com.noartcode.theprice.ui.presentation.payment.edit

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi
import br.com.noartcode.theprice.ui.views.BottomCircularButton
import br.com.noartcode.theprice.ui.views.ConfirmPaymentChangeDialog
import br.com.noartcode.theprice.ui.views.DateEditFieldView
import br.com.noartcode.theprice.ui.views.PriceEditField
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaymentScreen(
    modifier: Modifier = Modifier,
    state: EditPaymentUiState,
    onEvent: (event: EditPaymentEvent) -> Unit,
    onNavigateToEditBill : (String) -> Unit,
    onNavigateBack: () -> Unit
) {

    Scaffold (
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onNavigateToEditBill(state.billId) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val localFocus = LocalFocusManager.current
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        localFocus.clearFocus()
                    })
                }
            ,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = state.billName,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(20.dp))
            PriceEditField(
                fieldName = if (state.paymentStatus == PaymentUi.Status.PAYED)  "Payed amount" else "Amount to pay",
                fieldLabel = if (state.paymentStatus == PaymentUi.Status.PAYED) "Enter the payed amount" else "Enter the amount to pay",
                value = state.payedValue,
                hasError = state.priceHasError,
                onValueChanged = { onEvent(EditPaymentEvent.OnPriceChanged(it)) },
            )
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = { onEvent(EditPaymentEvent.OnStatusChanged) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.paymentStatus == PaymentUi.Status.PAYED) Color.Magenta else Color.LightGray
                )
            ) {
                Text(if (state.paymentStatus == PaymentUi.Status.PAYED) "Paid" else "Unpaid")
            }
            BottomCircularButton(
                isEnable = state.canSave,
                icon = Icons.Filled.Check,
                color = Color.Magenta,
                onClick = { onEvent(EditPaymentEvent.OnSave) }
            )
        }
        if (state.askingConfirmation) {
            ConfirmPaymentChangeDialog(
                onDismiss = { onEvent(EditPaymentEvent.OnDismissConfirmationDialog) },
                onConfirmToCurrent = { onEvent(EditPaymentEvent.OnChangeOnlyCurrentPayment) },
                onConfirmToAll = { onEvent(EditPaymentEvent.OnChangeAllFuturePayments) }
            )
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }
    
}


@Preview
@Composable
fun EditPaymentScreen_PendingPreview() {
    EditPaymentScreen(
        state = EditPaymentUiState(
            billId = "1",
            billName = "Netflix Subscription",
            paymentStatus = PaymentUi.Status.PENDING,
            payedValue = "15.99",
            paidDateTitle = "Mar 15, 2026",
            paidAtDate = 1741996800000L,
            priceHasChanged = true
        ),
        onEvent = {},
        onNavigateToEditBill = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun EditPaymentScreen_PaidPreview() {
    EditPaymentScreen(
        state = EditPaymentUiState(
            billId = "2",
            billName = "Spotify Premium",
            paymentStatus = PaymentUi.Status.PAYED,
            payedValue = "9.99",
            paidDateTitle = "Mar 1, 2026",
            paidAtDate = 1741737600000L,
            priceHasChanged = false
        ),
        onEvent = {},
        onNavigateToEditBill = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun EditPaymentScreen_WithConfirmationDialog() {
    EditPaymentScreen(
        state = EditPaymentUiState(
            billId = "3",
            billName = "Gym Membership",
            paymentStatus = PaymentUi.Status.PENDING,
            payedValue = "45.00",
            paidDateTitle = "Mar 20, 2026",
            paidAtDate = 1742428800000L,
            askingConfirmation = true,
            priceHasChanged = true
        ),
        onEvent = {},
        onNavigateToEditBill = {},
        onNavigateBack = {}
    )
}

