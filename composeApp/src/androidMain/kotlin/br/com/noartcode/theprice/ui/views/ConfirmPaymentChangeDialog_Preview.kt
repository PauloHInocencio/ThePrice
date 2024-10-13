package br.com.noartcode.theprice.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConfirmPaymentChangeDialog_Preview() {
    MaterialTheme {
        Scaffold { paddingValues ->
            ConfirmPaymentChangeDialog(
                modifier = Modifier.padding(paddingValues),
                onDismiss = { /*TODO*/ },
                onConfirmToCurrent = { /*TODO*/ },
                onConfirmToAll = {}
            )
        }
    }

}