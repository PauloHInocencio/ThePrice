package br.com.noartcode.theprice.ui.presentation.newbill

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillUiState

@Preview
@Composable
fun NewBillScreen_Preview(){
    NewBillScreen(
        state = NewBillUiState(),
        onEvent = {},
        onNavigateBack = {},
    )
}