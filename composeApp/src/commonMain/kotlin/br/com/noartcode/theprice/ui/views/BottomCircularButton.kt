package br.com.noartcode.theprice.ui.views




import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun BottomCircularButton(
    onClick:() -> Unit,
    modifier: Modifier = Modifier,
    isEnable:Boolean = true,
    icon:ImageVector = Icons.Default.Add,
    color:Color = Color.Unspecified
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Button(
            modifier = Modifier
                .padding(30.dp)
                .size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = color,
                contentColor = Color.White
            ),
            enabled = isEnable,
            onClick = onClick
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = icon,
                contentDescription = null
            )
        }
    }
}