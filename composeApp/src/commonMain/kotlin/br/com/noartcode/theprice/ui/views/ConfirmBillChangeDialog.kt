package br.com.noartcode.theprice.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmBillChangeDialog(
    onDismiss: () -> Unit,
    onConfirmToAllPayments: () -> Unit,
    onConfirmToFuturePayments: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmationDialog(
        description = "This change affects recurring payments.\nWhich payments should be updated?",
        primaryButtonText = "All payments",
        onPrimaryButtonClick = onConfirmToAllPayments,
        secondaryButtonText = "Current and future",
        onSecondaryButtonClick = onConfirmToFuturePayments,
        cancelButtonText = "Cancel",
        onDismiss = onDismiss,
        modifier = modifier,
    )
}
