package br.com.noartcode.theprice.ui.presentation.newbill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillEvent
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillUiState
import br.com.noartcode.theprice.ui.views.BottomCircularButton
import br.com.noartcode.theprice.ui.views.NormalEditField

@Composable
fun NewBillScreen(
    modifier: Modifier = Modifier,
    state:NewBillUiState,
    onEvent: (event:NewBillEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NormalEditField(
                modifier = Modifier.padding(20.dp),
                fieldName = "Price",
                fieldLabel = "enter the bill's price",
                value = "",
                onValueChanged = {},
                keyboardOptions = KeyboardOptions().copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(10.dp))
            NormalEditField(
                modifier = Modifier.padding(20.dp),
                fieldName = "Name",
                fieldLabel = "enter the bill's name",
                value = "",
                onValueChanged = {},
                keyboardOptions = KeyboardOptions().copy(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(10.dp))
            NormalEditField(
                modifier = Modifier.padding(20.dp),
                fieldName = "Due date",
                fieldLabel = "enter the bill's due date",
                value = "",
                onValueChanged = {},
                keyboardOptions = KeyboardOptions().copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(Modifier.height(10.dp))
            NormalEditField(
                modifier = Modifier.padding(20.dp),
                fieldName = "Description",
                fieldLabel = "enter the description (optional)",
                value = "",
                onValueChanged = {},
                keyboardOptions = KeyboardOptions().copy(
                    imeAction = ImeAction.Done
                )
            )
            BottomCircularButton(
                icon = Icons.Filled.Check,
                color = Color.Magenta,
                onClick = onNavigateBack
            )
        }
    }
}