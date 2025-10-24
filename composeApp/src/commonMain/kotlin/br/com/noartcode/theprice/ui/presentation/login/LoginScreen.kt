package br.com.noartcode.theprice.ui.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.app_logo
import theprice.composeapp.generated.resources.ic_google_logo

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToNewAccount: () -> Unit,
    modifier: Modifier = Modifier,
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(uiState) {
                if (uiState.errorMessage != null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = uiState.errorMessage,
                            withDismissAction = false,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                } else if (uiState.isAuthenticated) {
                    onNavigateToHome()
                }
            }

            LoginIconContainer()
            LoginEmailAndPasswordFieldsContainer(uiState, onEvent)
            LoginCreateAccountTextButtonContainer(uiState, onNavigateToNewAccount)
            Spacer(modifier.height(30.dp))
            LoginButtonsContainer(uiState, onEvent)

        }
    }
}



@Composable
private fun LoginIconContainer(){
    Column (
        modifier = Modifier.padding(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(42.dp),
            painter = painterResource(Res.drawable.app_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "The Price",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Of your freedom",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun LoginEmailAndPasswordFieldsContainer(
    uiState : LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {

    var passwordIsVisible by rememberSaveable { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            isError = uiState.emailHasError,
            modifier = Modifier.fillMaxWidth(),
            value = uiState.email,
            onValueChange = { onEvent(LoginEvent.OnEmailChanged(it)) },
            enabled = uiState.shouldEnableActions,
            placeholder = {
                Text(
                    text = "Email",
                    fontSize = 16.sp,
                    style = TextStyle(
                        color = Color.DarkGray.copy(alpha = 0.8f)
                    ),
                    color = LocalContentColor.current
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            isError = uiState.passwordHasError,
            modifier = Modifier.fillMaxWidth(),
            value = uiState.password,
            enabled = uiState.shouldEnableActions,
            onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) },
            trailingIcon = {
                IconButton(onClick = { passwordIsVisible = !passwordIsVisible }) {
                    Icon(
                        imageVector = if (passwordIsVisible)
                            Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordIsVisible)
                            "Hide password" else "Show password",
                        tint = LocalContentColor.current
                    )
                }
            },
            placeholder = {
                Text(
                    text = "Password",
                    fontSize = 16.sp,
                    style = TextStyle(
                        color = Color.DarkGray.copy(alpha = 0.8f)
                    ),
                    color = LocalContentColor.current
                )
            },
            singleLine = true,
            visualTransformation = if (passwordIsVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

    }
}

@Composable
private fun LoginCreateAccountTextButtonContainer(
    uiState : LoginUiState,
    onNavigateToAddAccount: () -> Unit,

) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = onNavigateToAddAccount,
            enabled = uiState.shouldEnableActions,
            content = {
                Text("Create Account")
            }
        )
    }
}

@Composable
private fun LoginButtonsContainer(
    uiState : LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = uiState.shouldEnableActions && uiState.canLogin,
        onClick = { }) {
        if (uiState.isAuthenticatingWithPassword) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = LocalContentColor.current,
                    strokeWidth = 2.dp
                )
            }
        } else {
            Text(
                text = "Log In",
                fontWeight = FontWeight.SemiBold
            )
        }

    }
    Spacer(modifier = Modifier.height(20.dp))
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = uiState.shouldEnableActions,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray.copy(alpha = 0.5f)
        ),
        onClick = { onEvent(LoginEvent.LoginInWithGoogle) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (uiState.isAuthenticatingWithGoogle) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = LocalContentColor.current,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_google_logo),
                    contentDescription = null,
                    alpha = if (uiState.shouldEnableActions) 1f else 0.5f
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    "Continue with Google",
                    color = if (uiState.shouldEnableActions) Color.Black else LocalContentColor.current,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreen_Preview() {
    LoginScreen(
        uiState = LoginUiState(),
        onEvent = { },
        onNavigateToHome = { },
        onNavigateToNewAccount = { },
    )
}