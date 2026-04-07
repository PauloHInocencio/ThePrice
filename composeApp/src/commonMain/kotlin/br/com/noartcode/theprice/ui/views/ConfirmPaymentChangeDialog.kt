package br.com.noartcode.theprice.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmPaymentChangeDialog(
    onDismiss: () -> Unit,
    onConfirmToCurrent: () -> Unit,
    onConfirmToAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmationDialog(
        description = "This entry repeats on other dates. \nWhat would you like to do?",
        primaryButtonText = "Update only this one",
        onPrimaryButtonClick = onConfirmToCurrent,
        secondaryButtonText = "This and the next ones",
        onSecondaryButtonClick = onConfirmToAll,
        cancelButtonText = "Cancel",
        onDismiss = onDismiss,
        modifier = modifier,
    )
}