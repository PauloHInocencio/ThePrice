package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.ui.views.BigText
import br.com.noartcode.theprice.ui.views.BottomCircularButton

@Composable
fun PaymentsScreen(
    modifier: Modifier = Modifier,
    onNavigateToNewBill: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            Spacer(Modifier.height(30.dp))
            BigText(title = "Payment Screen")
            Spacer(Modifier.height(30.dp))
            BottomCircularButton(
                onClick = onNavigateToNewBill
            )
        }
    }
}