package br.com.noartcode.theprice.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.bill_change_recurring_prompt
import theprice.composeapp.generated.resources.all_payments
import theprice.composeapp.generated.resources.current_and_future

@Composable
fun ConfirmBillChangeDialog(
    onDismiss: () -> Unit,
    onConfirmToAllPayments: () -> Unit,
    onConfirmToFuturePayments: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmationDialog(
        description = stringResource(Res.string.bill_change_recurring_prompt),
        primaryButtonText = stringResource(Res.string.all_payments),
        onPrimaryButtonClick = onConfirmToAllPayments,
        secondaryButtonText = stringResource(Res.string.current_and_future),
        onSecondaryButtonClick = onConfirmToFuturePayments,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}
