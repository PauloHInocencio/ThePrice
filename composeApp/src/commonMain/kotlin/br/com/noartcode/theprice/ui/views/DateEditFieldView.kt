package br.com.noartcode.theprice.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.noartcode.theprice.ui.theme.FieldLabelStyle

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
            style = FieldLabelStyle
        )
        DatePickerButtonView(
            title = dateTitle,
            selectedDate = selectedDate,
            onSelectDate = onSelectDate,
        )
    }
}