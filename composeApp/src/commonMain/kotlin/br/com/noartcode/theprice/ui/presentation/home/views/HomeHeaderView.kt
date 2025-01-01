package br.com.noartcode.theprice.ui.presentation.home.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeHeaderView(
    title:String,
    currentMonth: () -> Unit,
    previousMonth: () -> Unit,
    nextMonth : () -> Unit,
    canGoBack:Boolean,
    canGoNext:Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 16.dp)
            .semantics {
                if (title.isNotEmpty()) {
                    val (month, year) = title.split("-").map { it.trim() }
                    contentDescription = "Bills of $month $year"
                }
            }
        ,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = previousMonth,
            enabled = canGoBack
        ) {
            if(canGoBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null
                )
            }
        }
        
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = currentMonth),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
        )

        IconButton(
            onClick = nextMonth,
            enabled = canGoNext
        ) {
            if(canGoNext){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}