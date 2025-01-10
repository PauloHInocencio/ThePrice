package br.com.noartcode.theprice.ui.presentation.user.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Scaffold { innerPadding ->

        Column(modifier =  modifier
            .padding(innerPadding)
            .padding(20.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(state.singInStatus)
            Spacer(modifier.height(30.dp))
            Button(onClick = { onEvent(AccountEvent.SignInWithGoogle)}) {
                Text("Sign in with Google")
            }

        }
    }
}