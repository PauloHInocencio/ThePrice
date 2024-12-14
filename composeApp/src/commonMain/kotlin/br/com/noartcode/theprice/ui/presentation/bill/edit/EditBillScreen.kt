package br.com.noartcode.theprice.ui.presentation.bill.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.com.noartcode.theprice.ui.presentation.bill.add.BillScreenContent
import br.com.noartcode.theprice.ui.presentation.bill.edit.model.EditBillEvent
import br.com.noartcode.theprice.ui.presentation.bill.edit.model.EditBillUiState
import br.com.noartcode.theprice.ui.views.ConfirmBillDeletionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillScreen(
    modifier: Modifier = Modifier,
    state: EditBillUiState,
    onEvent: (event: EditBillEvent) -> Unit,
    onNavigateHome: () -> Unit
) {
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
            selectedDateTitle = state.billingStartDateTitle,
            selectedDate = state.billingStartDate,
            onSelectedDateChanged = { date -> onEvent(EditBillEvent.OnBillingStartDateChanged(date)) },
            description = state.description ?: "",
            onDescriptionChanged = { onEvent(EditBillEvent.OnDescriptionChanged(it)) },
            canSave = state.canSave,
            onSaveClicked = { onEvent(EditBillEvent.OnSave) },
        )
        if (state.showingConfirmationDialog) {
            ConfirmBillDeletionDialog(
                onDismiss = { onEvent(EditBillEvent.OnDismissConfirmationDialog) },
                onConfirmDeletion = { onEvent(EditBillEvent.OnDeleteBillConfirmed) },
            )
        }
    }

    LaunchedEffect(state.canClose) {
        if (state.canClose) onNavigateHome()
    }
}