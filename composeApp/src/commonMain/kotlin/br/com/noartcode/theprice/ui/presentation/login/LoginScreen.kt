package br.com.noartcode.theprice.ui.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
) {

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
                                    onEvent(LoginEvent.ErrorMessageDismissed)
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

            LaunchedEffect(uiState) {
                if (uiState is LoginUiState.AuthError) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = uiState.errorMessage,
                            withDismissAction = false,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
                if (uiState is LoginUiState.LoggedIn) {
                    onNavigateToHome()
                }
            }


            when(uiState) {
                is LoginUiState.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {
                    Spacer(modifier.height(30.dp))
                    Button(
                        enabled = (uiState is LoginUiState.AuthError).not(),
                        onClick = { onEvent(LoginEvent.LoginInWithGoogle)}) {
                        Text("Sign in with Google")
                    }
                }
            }

        }

    }

}