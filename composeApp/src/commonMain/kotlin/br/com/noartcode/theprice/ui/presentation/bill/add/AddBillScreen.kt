package br.com.noartcode.theprice.ui.presentation.bill.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.ui.views.BottomCircularButton
import br.com.noartcode.theprice.ui.views.DateEditFieldView
import br.com.noartcode.theprice.ui.views.NormalEditField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillScreen(
    modifier: Modifier = Modifier,
    state: AddBillUiState,
    onEvent: (event: AddBillEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
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
            onNameChanged = { onEvent(AddBillEvent.OnNameChanged(name = it)) },
            price = state.price,
            priceHasError = state.priceHasError,
            onPriceChanged = { onEvent(AddBillEvent.OnPriceChanged(it)) },
            selectedDateTitle = state.billingStartDateTitle,
            selectedDate = state.billingStartDate,
            onSelectedDateChanged = { date -> onEvent(AddBillEvent.OnBillingStartDateChanged(date)) },
            description = state.description ?: "",
            onDescriptionChanged = { onEvent(AddBillEvent.OnDescriptionChanged(it)) },
            canSave = state.canSave,
            onSaveClicked = { onEvent(AddBillEvent.OnSave) },
        )
    }


    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }
}


@Composable
fun BillScreenContent(
    name: String,
    nameHasError:Boolean = false,
    onNameChanged: (String) -> Unit,
    price:String,
    priceHasError:Boolean = false,
    onPriceChanged : (String) -> Unit,
    selectedDateTitle:String,
    selectedDate:Long,
    onSelectedDateChanged: (Long) -> Unit,
    description:String = "",
    onDescriptionChanged : (String) -> Unit,
    canSave:Boolean = false,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        NormalEditField(
            modifier = Modifier.padding(20.dp),
            fieldName = "Price",
            fieldLabel = "enter the bill's price",
            value = price,
            hasError = priceHasError,
            onValueChanged = onPriceChanged,
            keyboardOptions = KeyboardOptions().copy(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            )
        )
        Spacer(Modifier.height(10.dp))
        NormalEditField(
            modifier = Modifier.padding(20.dp),
            fieldName = "Name",
            fieldLabel = "enter the bill's name",
            value = name,
            hasError = nameHasError,
            onValueChanged = onNameChanged,
            keyboardOptions = KeyboardOptions().copy(
                imeAction = ImeAction.Next
            )
        )
        Spacer(Modifier.height(10.dp))
        DateEditFieldView(
            modifier = Modifier.padding(20.dp),
            title = "Vencimento",
            dateTitle = selectedDateTitle,
            selectedDate = selectedDate,
            onSelectDate = onSelectedDateChanged,
        )
        Spacer(Modifier.height(10.dp))
        NormalEditField(
            modifier = Modifier.padding(20.dp),
            fieldName = "Description",
            fieldLabel = "enter the description (optional)",
            value = description,
            onValueChanged = onDescriptionChanged,
            keyboardOptions = KeyboardOptions().copy(
                imeAction = ImeAction.Done
            )
        )
        BottomCircularButton(
            isEnable = canSave,
            icon = Icons.Filled.Check,
            color = Color.Magenta,
            onClick = onSaveClicked
        )
    }
}