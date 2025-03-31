package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.ui.presentation.home.views.HomeHeaderView
import br.com.noartcode.theprice.ui.views.BottomCircularButton
import br.com.noartcode.theprice.ui.presentation.home.views.PaymentItemView
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToAddBill: () -> Unit,
    onNavigateToEditPayment: (paymentId:String) -> Unit,
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
            .padding(6.dp)
            .fillMaxSize()
        ) {
            HomeHeaderView(
                modifier = Modifier.fillMaxWidth(),
                title = state.monthName,
                currentMonth = { onEvent(HomeEvent.OnGoToCurrentMonth) },
                nextMonth = { onEvent(HomeEvent.OnGoToNexMonth) },
                previousMonth = {onEvent(HomeEvent.OnBackToPreviousMonth) },
                canGoNext = state.canGoNext,
                canGoBack = state.canGoBack
            )
            LazyColumn {
                itemsIndexed(
                    items = state.payments,
                    key = { _, payment: PaymentUi -> payment.id }) { _, payment ->
                    PaymentItemView(
                        payment = payment,
                        onStatusClicked = {
                            onEvent(
                                HomeEvent.OnPaymentStatusClicked(
                                    id = payment.id,
                                    status = payment.status,
                                )
                            )},
                        onPaymentClicked = onNavigateToEditPayment
                    )
                }
            }
            BottomCircularButton(
                onClick = onNavigateToAddBill
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