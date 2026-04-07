package br.com.noartcode.theprice.ui.presentation.bill.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.ui.presentation.bill.add.BillScreenContent
import br.com.noartcode.theprice.ui.views.ConfirmBillChangeDialog
import br.com.noartcode.theprice.ui.views.ConfirmBillDeletionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillScreen(
    state: EditBillUiState,
    onEvent: (event: EditBillEvent) -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    // When error
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = false,
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    LaunchedEffect(state.canClose) {
        if (state.canClose) onNavigateHome()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(EditBillEvent.OnDeleteBill )}) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        action = {
                            TextButton(
                                onClick = {
                                    data.dismiss()
                                    onEvent(EditBillEvent.OnDismissErrorMessage)
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    ) {
                        Text(data.visuals.message)
                    }
                }
            )
        }
    ) { innerPadding ->
        BillScreenContent(
            modifier = modifier.padding(innerPadding),
            name = state.name,
            nameHasError = state.nameHasError,
            onNameChanged = { onEvent(EditBillEvent.OnNameChanged(name = it)) },
            price = state.price,
            priceHasError = state.priceHasError,
            onPriceChanged = { onEvent(EditBillEvent.OnPriceChanged(it)) },
            selectedDate = state.billingStartDate,
            onSelectedDateChanged = { date -> onEvent(EditBillEvent.OnBillingStartDateChanged(date)) },
            description = state.description ?: "",
            onDescriptionChanged = { onEvent(EditBillEvent.OnDescriptionChanged(it)) },
            canChangeBillingStartDate = false,
            canSave = state.canSave,
            onSaveClicked = { onEvent(EditBillEvent.OnSave) },
        )
        if (state.showingConfirmationDialog) {
            ConfirmBillDeletionDialog(
                onDismiss = { onEvent(EditBillEvent.OnDismissConfirmationDialog) },
                onConfirmDeletion = { onEvent(EditBillEvent.OnDeleteBillConfirmed) },
            )
        }
        if (state.showingChangePropagationDialog) {
            ConfirmBillChangeDialog(
                onDismiss = { onEvent(EditBillEvent.OnDismissChangePropagationDialog) },
                onConfirmToAllPayments = { onEvent(EditBillEvent.OnApplyChangesToAllPayments) },
                onConfirmToFuturePayments = { onEvent(EditBillEvent.OnApplyChangesToFuturePayments) },
            )
        }
    }

}