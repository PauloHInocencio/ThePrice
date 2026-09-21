package br.com.noartcode.theprice.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.payment_repeats_prompt
import theprice.composeapp.generated.resources.update_only_this_one
import theprice.composeapp.generated.resources.this_and_next_ones

@Composable
fun ConfirmPaymentChangeDialog(
    onDismiss: () -> Unit,
    onConfirmToCurrent: () -> Unit,
    onConfirmToAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmationDialog(
        description = stringResource(Res.string.payment_repeats_prompt),
        primaryButtonText = stringResource(Res.string.update_only_this_one),
        onPrimaryButtonClick = onConfirmToCurrent,
        secondaryButtonText = stringResource(Res.string.this_and_next_ones),
        onSecondaryButtonClick = onConfirmToAll,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}