package br.com.noartcode.theprice.ui.presentation.newBill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.noartcode.theprice.ui.views.BottomCircularButton

@Composable
fun NewBillScreen(
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(text = "New Bill")
            BottomCircularButton(
                icon = Icons.Filled.Check,
                onClick = {}
            )
        }
    }
}