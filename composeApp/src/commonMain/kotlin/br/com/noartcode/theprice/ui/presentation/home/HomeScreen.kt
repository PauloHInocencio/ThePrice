package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import br.com.noartcode.theprice.ui.presentation.home.model.HomeUiState
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.ui.views.BottomCircularButton
import br.com.noartcode.theprice.ui.views.PaymentItemView
import kotlinx.coroutines.launch

@Composable
fun PaymentsScreen(
    modifier: Modifier = Modifier,
    state:HomeUiState,
    onEvent: () -> Unit,
    onNavigateToNewBill: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            LazyColumn {
                itemsIndexed(
                    items = state.payments,
                    key = { _, payment:PaymentUi-> payment.id }) { _, payment ->
                    PaymentItemView(
                        payment = payment,
                        onStatusClicked = {},
                        onPaymentClicked = {}
                    )
                }
            }
            BottomCircularButton(
                onClick = onNavigateToNewBill
            )
        }
    }

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage
        if(message != null) {
            scope.launch {
                val result = snackbarHostState
                    .showSnackbar(
                        message = message,
                        actionLabel = "OK",
                        duration = SnackbarDuration.Indefinite
                    )
                when (result) {
                    SnackbarResult.ActionPerformed -> {

                    }
                    SnackbarResult.Dismissed -> {

                    }
                }
            }
        }
    }
}