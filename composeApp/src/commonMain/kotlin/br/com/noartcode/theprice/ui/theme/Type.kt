package br.com.noartcode.theprice.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Text style for field labels throughout the app.
 * Used for labels above input fields.
 */
val FieldLabelStyle = TextStyle(
    color = Color.DarkGray.copy(alpha = 0.8f),
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold
)
