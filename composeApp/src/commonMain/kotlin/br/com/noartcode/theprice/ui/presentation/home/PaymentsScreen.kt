package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.noartcode.theprice.ui.views.BottomCircularButton

@Composable
fun PaymentsScreen(
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            Text(text = "Payment Screen")
            BottomCircularButton(
                onClick = {}
            )
        }
    }
}