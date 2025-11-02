package br.com.noartcode.theprice.ui.presentation.home.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.pric_logo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeToolbar(
    modifier: Modifier = Modifier,
    onMenuItemClick: (Int) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
) {
    var isDropDownOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val menuItems = listOf("Profile","Logout")

    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.pric_logo),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ThePrice",
                    fontWeight = FontWeight.SemiBold,
                    color  = Color(red =69,	green = 154, blue = 229),
                    fontSize = 18.sp
                )
            }
        },
        actions = {
            Box {
                DropdownMenu(
                    expanded = isDropDownOpen,
                    onDismissRequest = {
                        isDropDownOpen = false
                    }
                ) {
                    menuItems.forEachIndexed { index, description ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    isDropDownOpen = false
                                    onMenuItemClick(index)
                                }
                                .fillMaxWidth()
                                .padding(16.dp)
                        ){
                            Text(description)
                        }
                    }
                }
                IconButton(onClick = {
                    isDropDownOpen = true
                }){
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                    )
                }
            }
        }
    )


}



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun HomeToolbar_Preview() {
    HomeToolbar(
        onMenuItemClick = {},
    )
}
