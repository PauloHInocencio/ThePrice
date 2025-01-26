package br.com.noartcode.theprice.ui.presentation.user.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import br.com.noartcode.theprice.ui.presentation.user.account.model.AccountEvent
import br.com.noartcode.theprice.ui.presentation.user.account.model.AccountUiState


@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    state: AccountUiState,
    onEvent: (event: AccountEvent) -> Unit,
    onNavigateBack: () -> Unit
) {

    val clipboardManager = LocalClipboardManager.current

    Scaffold { innerPadding ->

        Column(modifier =  modifier
            .padding(innerPadding)
            .padding(20.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.clickable(
                    onClick = { clipboardManager.setText(AnnotatedString(state.singInStatus)) }
                ),
                text = state.singInStatus
            )
            Spacer(modifier.height(30.dp))
            Button(onClick = { onEvent(AccountEvent.SignInWithGoogle)}) {
                Text("Sign in with Google")
            }

        }
    }
}

@Composable
fun CopyButton(
    text : String = "Copy",
    onClick : ()-> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF5F5F5),
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .height(40.dp)
            .padding(horizontal = 8.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

        }
    }
}