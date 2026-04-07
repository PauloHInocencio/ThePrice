package br.com.noartcode.theprice.ui.presentation.bill.add

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.isValid
import br.com.noartcode.theprice.ui.views.BottomCircularButton
import br.com.noartcode.theprice.ui.views.DayPickerView
import br.com.noartcode.theprice.ui.views.MonthYearPickerView
import br.com.noartcode.theprice.ui.views.NormalEditField
import br.com.noartcode.theprice.ui.views.PriceEditField
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    selectedDate: DayMonthAndYear,
    onSelectedDateChanged: (DayMonthAndYear) -> Unit,
    description:String = "",
    onDescriptionChanged : (String) -> Unit,
    canSave:Boolean = false,
    canChangeBillingStartDate: Boolean = true,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val localFocus = LocalFocusManager.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocus.clearFocus()
                })
            }
    ) {
        PriceEditField(
            modifier = Modifier.padding(20.dp),
            fieldName = "Price",
            fieldLabel = "enter the bill's price",
            value = price,
            hasError = priceHasError,
            onValueChanged = onPriceChanged,
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonthYearPickerView(
                title = "Billing Start Date",
                selectedMonth = selectedDate.month,
                selectedYear = selectedDate.year,
                enabled = canChangeBillingStartDate,
                onSelectMonthYear = { month, year ->
                    onSelectedDateChanged(selectedDate.copy(month = month, year = year))
                }
            )
            DayPickerView(
                title = "Pay Day",
                selectedDay = selectedDate.day,
                onSelectDay = { day ->
                    val newDate = selectedDate.copy(day = day)
                    if (newDate.isValid()){
                        onSelectedDateChanged(selectedDate.copy(day = day))
                    }
                },
            )
        }
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

@Preview
@Composable
fun AddBillScreenEmptyPreview() {
    AddBillScreen(
        state = AddBillUiState(),
        onEvent = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun AddBillScreenFilledPreview() {
    AddBillScreen(
        state = AddBillUiState(
            name = "Internet Bill",
            price = "89.99",
            billingStartDateTitle = "01/03/2026",
            billingStartDate = DayMonthAndYear(day = 1, month = 3, year = 2026),
            description = "Monthly internet subscription"
        ),
        onEvent = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun BillScreenContentEmptyPreview() {
    Surface {
        BillScreenContent(
            name = "",
            nameHasError = true,
            onNameChanged = {},
            price = "",
            priceHasError = true,
            onPriceChanged = {},
            selectedDate = DayMonthAndYear.EMPTY,
            onSelectedDateChanged = {},
            description = "",
            onDescriptionChanged = {},
            canSave = false,
            onSaveClicked = {}
        )
    }
}

@Preview
@Composable
fun BillScreenContentFilledPreview() {
    Surface {
        BillScreenContent(
            name = "Netflix Subscription",
            nameHasError = false,
            onNameChanged = {},
            price = "15.99",
            priceHasError = false,
            onPriceChanged = {},
            selectedDate = DayMonthAndYear(day = 14, month = 3, year = 2026),
            onSelectedDateChanged = {},
            description = "Premium plan for 4 screens",
            onDescriptionChanged = {},
            canSave = true,
            onSaveClicked = {}
        )
    }
}