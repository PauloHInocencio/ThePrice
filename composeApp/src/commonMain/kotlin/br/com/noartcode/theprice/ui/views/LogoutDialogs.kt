package br.com.noartcode.theprice.ui.views

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.cancel
import theprice.composeapp.generated.resources.logout
import theprice.composeapp.generated.resources.logout_anyway
import theprice.composeapp.generated.resources.logout_confirm_message
import theprice.composeapp.generated.resources.logout_confirm_title
import theprice.composeapp.generated.resources.logout_offline_message
import theprice.composeapp.generated.resources.logout_offline_title
import theprice.composeapp.generated.resources.logout_pending_changes

sealed interface LogoutDialog {
    data class Confirm(val pendingChanges: Int) : LogoutDialog
    data object Offline : LogoutDialog
}

@Composable
fun LogoutDialogHost(
    dialog: LogoutDialog?,
    onConfirm: () -> Unit,
    onForceLogout: () -> Unit,
    onDismiss: () -> Unit,
) {
    when (dialog) {
        null -> Unit
        is LogoutDialog.Confirm -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(Res.string.logout_confirm_title)) },
            text = {
                Text(
                    if (dialog.pendingChanges > 0) stringResource(Res.string.logout_pending_changes, dialog.pendingChanges)
                    else stringResource(Res.string.logout_confirm_message)
                )
            },
            confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(Res.string.logout)) } },
            dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
        )
        LogoutDialog.Offline -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(Res.string.logout_offline_title)) },
            text = { Text(stringResource(Res.string.logout_offline_message)) },
            confirmButton = { TextButton(onClick = onForceLogout) { Text(stringResource(Res.string.logout_anyway)) } },
            dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
        )
    }
}
