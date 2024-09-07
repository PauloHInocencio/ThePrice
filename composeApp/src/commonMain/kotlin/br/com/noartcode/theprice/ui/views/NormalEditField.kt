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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun NormalEditField(
    value:String,
    fieldName: String,
    fieldLabel:String,
    hasError:Boolean = false,
    onValueChanged:(String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.None
    ),
    modifier: Modifier = Modifier
) {
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
            value = value,
            onValueChange = { onValueChanged(it) },
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
            keyboardOptions = keyboardOptions
        )
    }
}


