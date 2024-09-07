package br.com.noartcode.theprice.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun DateEditFieldView(
    title:String,
    dateTitle:String,
    selectedDate:Long,
    onSelectDate: (Long) -> Unit,
    modifier:Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            maxLines = 1,
            style = TextStyle(
                color = Color.DarkGray.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
        DatePickerButtonView(
            title = dateTitle,
            selectedDate = selectedDate,
            onSelectDate = onSelectDate,
        )
    }
}