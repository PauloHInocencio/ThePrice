package br.com.noartcode.theprice.ui.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButtonView(
    title:String,
    selectedDate:Long,
    onSelectDate:(Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState()
    var showDatePickerDialog by remember {
        mutableStateOf(false)
    }

    datePickerState.selectedDateMillis = selectedDate

    TextButton(
        onClick = { showDatePickerDialog = true },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                maxLines = 1,
                color = Color.DarkGray.copy(alpha = 0.8f),
                fontSize = 21.sp,
            )
        }
    }

    if(showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton( onClick = {
                    showDatePickerDialog = false
                    datePickerState.let {
                        it.selectedDateMillis?.let { millis ->
                            onSelectDate(millis)
                        }
                    }
                }) {
                    Text(
                        text = "OK",
                        fontSize = 18.sp,
                    )
                }
            }
        ){
            DatePicker(datePickerState)
        }
    }
}