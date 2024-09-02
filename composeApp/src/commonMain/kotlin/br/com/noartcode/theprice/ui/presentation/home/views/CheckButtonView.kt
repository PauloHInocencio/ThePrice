package br.com.noartcode.theprice.ui.presentation.home.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun CheckButtonView(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(26.dp)
            .border(
                border = BorderStroke(
                    1.dp,
                    SolidColor(Color.Black)
                ),
                shape = CircleShape
            )
            .clickable { onClick() }
            .clip(CircleShape)
            .padding(4.dp)
            .background(
                if (isChecked) Color.Black else Color.White,
                shape = CircleShape
            ),
    )
}