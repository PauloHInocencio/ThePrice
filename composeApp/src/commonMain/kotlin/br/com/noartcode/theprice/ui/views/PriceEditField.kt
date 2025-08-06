package br.com.noartcode.theprice.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PriceEditField(
    value:String,
    fieldName: String,
    fieldLabel:String,
    hasError:Boolean = false,
    onValueChanged:(String) -> Unit,
    modifier: Modifier = Modifier
) {
    // This is needed to keep cursor position at the end of the value
    var textFieldValueState by remember(value) {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length),
            )
        )
    }

    Column(modifier = modifier) {
        Text(
            text = fieldName,
            maxLines = 1,
            style = TextStyle(
                color = Color.DarkGray.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            isError = hasError,
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValueState,
            onValueChange = {
              onValueChanged(it.text)
            },
            placeholder = {
                Text(
                    text = fieldLabel,
                    style = TextStyle(
                        color = Color.DarkGray.copy(alpha = 0.8f),
                    )
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            )
        )
    }
}


