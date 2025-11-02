package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import br.com.noartcode.theprice.ui.presentation.home.views.HomeToolbar
import br.com.noartcode.theprice.ui.presentation.home.views.PaymentDateHeader
import br.com.noartcode.theprice.ui.presentation.home.views.PaymentItemView
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToAddBill: () -> Unit,
    onNavigateToEditPayment: (paymentId:String) -> Unit,
    onNavigateToAccount : () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = { HomeToolbar(onMenuItemClick = { index ->
            if (index == 0) { onNavigateToAccount() }
            if (index == 1) { onEvent(HomeEvent.OnLogoutUser) }
        }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick= onNavigateToAddBill
            ) {
                Icon(Icons.Default.Add, "Add new Bill")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
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
                state.paymentSection.forEach { sectionUi ->
                    stickyHeader(key = "header-${sectionUi.dueDay}") {
                        PaymentDateHeader(title = sectionUi.title)
                    }
                    items(
                        items = sectionUi.paymentUis,
                        key = { it.id }) { payment ->
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
            }
        }
    }
    LaunchedEffect(state) {
        if (state.errorMessage != null) {
            scope.launch {
                val result = snackbarHostState
                    .showSnackbar(
                        message = state.errorMessage,
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

        if (state.loggedOut) onNavigateToLogin()
    }
}


@Preview
@Composable
fun HomeScreen_Preview() {
    HomeScreen(
        state = HomeUiState(
            monthName = "October - 2025",
            canGoBack = true,
            canGoNext = true,
            paymentSection = listOf(
                PaymentSectionUi(
                    title = "Due on 5th",
                    dueDay = 5,
                    paymentUis = listOf(
                        PaymentUi(
                            id = "1",
                            statusDescription = "Paid",
                            billName = "Netflix Subscription",
                            price = "$15.99",
                            status = PaymentUi.Status.PAYED
                        ),
                        PaymentUi(
                            id = "2",
                            statusDescription = "Pending",
                            billName = "Spotify Premium",
                            price = "$9.99",
                            status = PaymentUi.Status.PENDING
                        )
                    )
                ),
                PaymentSectionUi(
                    title = "Due on 15th",
                    dueDay = 15,
                    paymentUis = listOf(
                        PaymentUi(
                            id = "3",
                            statusDescription = "Overdue",
                            billName = "Internet Bill",
                            price = "$79.99",
                            status = PaymentUi.Status.OVERDUE
                        ),
                        PaymentUi(
                            id = "4",
                            statusDescription = "Paid",
                            billName = "Electricity",
                            price = "$120.50",
                            status = PaymentUi.Status.PAYED
                        )
                    )
                ),
                PaymentSectionUi(
                    title = "Due on 25th",
                    dueDay = 25,
                    paymentUis = listOf(
                        PaymentUi(
                            id = "5",
                            statusDescription = "Pending",
                            billName = "Rent",
                            price = "$1,200.00",
                            status = PaymentUi.Status.PENDING
                        ),
                        PaymentUi(
                            id = "6",
                            statusDescription = "Pending",
                            billName = "Water Bill",
                            price = "$35.00",
                            status = PaymentUi.Status.PENDING
                        ),
                        PaymentUi(
                            id = "7",
                            statusDescription = "Paid",
                            billName = "Phone Bill",
                            price = "$45.00",
                            status = PaymentUi.Status.PAYED
                        )
                    )
                )
            )
        ),
        onEvent = {},
        onNavigateToAddBill = {},
        onNavigateToEditPayment = {},
        onNavigateToAccount = {},
        onNavigateToLogin = {},
    )
}