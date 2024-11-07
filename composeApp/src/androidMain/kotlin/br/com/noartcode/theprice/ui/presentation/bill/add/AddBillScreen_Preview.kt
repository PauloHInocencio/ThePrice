package br.com.noartcode.theprice.ui.presentation.bill.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.noartcode.theprice.ui.presentation.bill.add.model.AddBillUiState

@Preview
@Composable
fun AddBillScreen_Preview(){
    AddBillScreen(
        state = AddBillUiState(),
        onEvent = {},
        onNavigateBack = {},
    )
}