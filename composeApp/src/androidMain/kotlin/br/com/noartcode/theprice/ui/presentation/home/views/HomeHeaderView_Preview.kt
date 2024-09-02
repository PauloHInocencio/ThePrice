package br.com.noartcode.theprice.ui.presentation.home.views

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun HomeHeaderView_Preview() {

    Surface {
        HomeHeaderView(
            title = "September 2024",
            currentMonth = {},
            previousMonth = {  },
            nextMonth = {  },
            canGoBack = true,
            canGoNext = true
        )
    }

}