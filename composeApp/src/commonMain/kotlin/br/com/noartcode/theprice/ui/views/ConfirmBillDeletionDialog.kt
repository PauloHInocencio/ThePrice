package br.com.noartcode.theprice.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.stringResource
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.delete_bill_confirmation
import theprice.composeapp.generated.resources.delete
import theprice.composeapp.generated.resources.cancel

@Composable
fun ConfirmBillDeletionDialog(
    onDismiss:() -> Unit,
    onConfirmDeletion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(Res.string.delete_bill_confirmation))
                Spacer(Modifier.height(20.dp))
                OutlinedButton(onClick = onConfirmDeletion) {
                    Text(stringResource(Res.string.delete))
                }
                Button(onClick = onDismiss) {
                    Text(stringResource(Res.string.cancel))
                }

            }
        }
    }
}