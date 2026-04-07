package br.com.noartcode.theprice.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.ui.theme.FieldLabelStyle
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MonthYearPickerView(
    title: String,
    selectedMonth: Int,
    selectedYear: Int,
    enabled: Boolean,
    onSelectMonthYear: (month: Int, year:Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val minYear = currentDate.year - 10
    val maxYear = currentDate.year

    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.alpha(if (enabled) 1f else 0.38f)) {
        Text(title, style = FieldLabelStyle)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "${getMonthName(selectedMonth)} - $selectedYear",
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            ),
            modifier = Modifier
                .clickable(enabled = enabled) { showDialog = true }
                .padding(0.dp)
        )
    }

    if (showDialog) {
        MonthYearPickerDialog(
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            minYear = minYear,
            maxYear = maxYear,
            currentMonth = currentDate.month.number,
            currentYear = currentDate.year,
            onDismiss = { showDialog = false },
            onConfirm = { month, year ->
                onSelectMonthYear(month, year)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MonthYearPickerDialog(
    selectedMonth: Int,
    selectedYear: Int,
    minYear: Int,
    maxYear: Int,
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (month: Int, year: Int) -> Unit
){
    var tempMonth by remember { mutableStateOf(selectedMonth) }
    var tempYear by remember { mutableStateOf(selectedYear) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Billing Start Date") },
        text = {
            Column {

                // Month Dropdown
                var isMonthExpanded by remember { mutableStateOf(false) }
                Text("Month", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded =  isMonthExpanded,
                    onExpandedChange = { isMonthExpanded = it }
                ) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        value = getMonthName(tempMonth),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isMonthExpanded )},
                    )
                    ExposedDropdownMenu(
                        expanded = isMonthExpanded,
                        onDismissRequest = { isMonthExpanded = false }
                    ) {
                        (1..12).forEach { month ->

                            val isEnable = if (tempYear == currentYear) {
                                month <= currentMonth
                            } else { true }

                            DropdownMenuItem(
                                text = { Text(getMonthName(month)) },
                                onClick = {
                                    if (isEnable) {
                                        tempMonth = month
                                        isMonthExpanded = false
                                    }
                                },
                                enabled = isEnable
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))


                // Year Dropdown
                var isYearExpanded by remember { mutableStateOf(false)}
                Text("Year", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = isYearExpanded,
                    onExpandedChange = { isYearExpanded = it }
                ) {
                    TextField(
                        value = tempYear.toString(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isYearExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isYearExpanded,
                        onDismissRequest = { isYearExpanded = false }
                    ) {
                        (maxYear downTo minYear).forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = {
                                    tempYear = year
                                    isYearExpanded = false
                                    // If switching to current year and
                                    // selected month is in future, adjust month
                                    if (year == currentYear && tempMonth > currentMonth) {
                                        tempMonth = currentMonth
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(tempMonth, tempYear)}) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> "Invalid"
    }
}



@Preview
@Composable
private fun MonthYearPickerDialogPreview() {
    MaterialTheme {
        MonthYearPickerDialog(
            selectedMonth = 3,
            selectedYear = 2026,
            minYear = 2016,
            maxYear = 2026,
            currentMonth = 3,
            currentYear = 2026,
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun MonthYearPickerView_Preview() {
    MaterialTheme {
        Surface {
            MonthYearPickerView(
                title = "Billing Start Date",
                selectedMonth = 3,
                selectedYear = 2026,
                enabled = true,
                onSelectMonthYear = { _, _ -> }
            )
        }
    }
}

@Preview
@Composable
private fun MonthYearPickerViewDisable_Preview() {
    MaterialTheme {
        Surface {
            MonthYearPickerView(
                title = "Billing Start Date",
                selectedMonth = 3,
                selectedYear = 2026,
                enabled = false,
                onSelectMonthYear = { _, _ -> }
            )
        }
    }
}

