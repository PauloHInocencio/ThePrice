package br.com.noartcode.theprice.ui.presentation.bill.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun EditBillScreen_Preview() {
    EditBillScreen(
        state = EditBillUiState(),
        onEvent = { },
        onNavigateHome = { }
    )
}