package br.com.noartcode.theprice.ui.presentation.auth.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.ui.presentation.auth.account.model.AccountEvent
import br.com.noartcode.theprice.ui.presentation.auth.account.model.AccountUiState
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch


@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    state: AccountUiState,
    onEvent: (event: AccountEvent) -> Unit,
    onNavigateBack: () -> Unit
) {

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        action = {
                            TextButton(
                                onClick = {
                                    data.dismiss()
                                    onEvent( AccountEvent.ErrorMessageDismissed)
                                }
                            ) {
                                Text("Ok")
                            }
                        }
                    ) {
                        Text(data.visuals.message)
                    }
                }
            )
        },
    ) { innerPadding ->

        Column(modifier =  modifier
            .padding(innerPadding)
            .padding(20.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LaunchedEffect(state.errorMessage) {
                if (state.errorMessage != null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = state.errorMessage,
                            withDismissAction = false,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
            }


            if (state.loading) {
                CircularProgressIndicator()
            } else if(state.user != null) {
                UserProfileCard(
                    user = state.user
                )
                Button(onClick = {  onEvent(AccountEvent.LogOutUser)  }) {
                    Text("Logout")
                }
            } else {
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
}


@Composable
fun UserProfileCard(user: User, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.picture,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(100.dp).aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.email, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}