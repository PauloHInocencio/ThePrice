package br.com.noartcode.theprice.ui.views

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.ui.theme.FieldLabelStyle
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPickerView(
    title: String,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.sizeIn(maxWidth= 150.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(title, style = FieldLabelStyle)
        Spacer(Modifier.height(12.dp))
        ExposedDropdownMenuBox(
            modifier = Modifier.padding(0.dp),
            expanded = isExpanded, onExpandedChange = { isExpanded = it }
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            BasicTextField(
                modifier = Modifier.menuAnchor().padding(0.dp),
                value = "Every ${getDayWithOrdinal(selectedDay)}",
                onValueChange = {},
                readOnly = true,
                textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        innerTextField()
                        ExposedDropdownMenuDefaults.TrailingIcon(isExpanded)
                    }
                }
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                (1..31).forEach { day ->
                    DropdownMenuItem(
                        text = { Text(getDayWithOrdinal(day)) },
                        onClick = {
                            onSelectDay(day)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }


}



private fun getDayWithOrdinal(day: Int): String {
    val suffix = when {
        day in 11..13 -> "th"  // Special cases: 11th, 12th, 13th
        day % 10 == 1 -> "st"  // 1st, 21st, 31st
        day % 10 == 2 -> "nd"  // 2nd, 22nd
        day % 10 == 3 -> "rd"  // 3rd, 23rd
        else -> "th"           // 4th, 5th, 6th, 7th, 8th, 9th, 10th, etc.
    }
    return "$day$suffix"
}

@Preview
@Composable
private fun DayPickerView_Preview() {
    Surface {
        DayPickerView(
            title = "Pay Day",
            selectedDay = 10,
            onSelectDay = {},
        )
    }
}