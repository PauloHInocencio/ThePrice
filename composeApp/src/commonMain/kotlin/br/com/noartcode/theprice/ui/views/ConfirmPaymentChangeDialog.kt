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

@Composable
fun ConfirmPaymentChangeDialog(
    onDismiss:() -> Unit,
    onConfirmToCurrent: () -> Unit,
    onConfirmToAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("This entry repeats on other dates. \nWhat would you like to do?")
                Spacer(Modifier.height(20.dp))
                Button(onClick = onConfirmToCurrent) {
                    Text("Update only this one")
                }
                FilledTonalButton(onClick = onConfirmToAll) {
                    Text("This and the next ones")
                }
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }

            }
        }
    }
}