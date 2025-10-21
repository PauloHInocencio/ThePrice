package br.com.noartcode.theprice.ui.presentation.account.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.app_logo

@Composable
fun NewAccountScreen(
    uiState: NewAccountUiState,
    onEvent: (NewAccountEvent) -> Unit,
    onNavigateToLogin: () -> Unit,
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
                                    //onEvent(NewAccountUiState.ErrorMessageDismissed)
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
                /*if (uiState is NewAccountUiState.CreationError) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = uiState.errorMessage,
                            withDismissAction = false,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
                if (uiState is NewAccountUiState.Created) {
                    onNavigateToHome()
                }*/
            }

            NewAccountIconContainer(uiState, onEvent)
            NewAccountFormFieldsContainer(uiState, onEvent)
            NewAccountCreateAccountTextButtonContainer(uiState, onNavigateToLogin)
            Spacer(modifier.height(30.dp))
            NewAccountButtonContainer(uiState, onEvent)

        }
    }


}



@Composable
private fun NewAccountIconContainer(
    uiState : NewAccountUiState,
    onEvent: (NewAccountEvent) -> Unit
){
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
private fun NewAccountFormFieldsContainer(
    uiState : NewAccountUiState,
    onEvent: (NewAccountEvent) -> Unit
) {
    Column {
        OutlinedTextField(
            isError = false,
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = "Name",
                    fontSize = 16.sp,
                    style = TextStyle(
                        color = Color.DarkGray.copy(alpha = 0.8f)
                    )
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            isError = false,
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = "Email",
                    fontSize = 16.sp,
                    style = TextStyle(
                        color = Color.DarkGray.copy(alpha = 0.8f)
                    )
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
            isError = false,
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            placeholder = {
                Text(
                    text = "Password",
                    fontSize = 16.sp,
                    style = TextStyle(
                        color = Color.DarkGray.copy(alpha = 0.8f)
                    )
                )
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

    }
}

@Composable
private fun NewAccountCreateAccountTextButtonContainer(
    uiState : NewAccountUiState,
    onNavigateToLogin: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = onNavigateToLogin,
            content = {
                Text("Log In")
            }
        )
    }
}

@Composable
private fun NewAccountButtonContainer(
    uiState : NewAccountUiState,
    onEvent: (NewAccountEvent) -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = uiState.canSave,
        onClick = { }) {
        Text(
            text = "Sign Up",
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
private fun NewAccountScreen_Preview(){
    NewAccountScreen(
        uiState = NewAccountUiState(),
        onEvent = {},
        onNavigateToLogin = {}
    )
}