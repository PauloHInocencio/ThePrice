package br.com.noartcode.theprice.ui.presentation.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.noartcode.theprice.BuildKonfig
import br.com.noartcode.theprice.domain.model.AppCurrency
import br.com.noartcode.theprice.domain.model.AppLanguage
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.ui.views.LogoutDialogHost
import br.com.noartcode.theprice.ui.views.SettingsRow
import br.com.noartcode.theprice.ui.views.SpinnerSettingsRow
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.app_name
import theprice.composeapp.generated.resources.logout
import theprice.composeapp.generated.resources.not_authenticated
import theprice.composeapp.generated.resources.ok
import theprice.composeapp.generated.resources.profile_picture
import theprice.composeapp.generated.resources.settings_currency
import theprice.composeapp.generated.resources.settings_currency_brl
import theprice.composeapp.generated.resources.settings_currency_usd
import theprice.composeapp.generated.resources.settings_language
import theprice.composeapp.generated.resources.settings_language_english
import theprice.composeapp.generated.resources.settings_language_portuguese
import theprice.composeapp.generated.resources.settings_restart_required
import theprice.composeapp.generated.resources.settings_title
import theprice.composeapp.generated.resources.settings_version_footer
import theprice.composeapp.generated.resources.sign_in_with_google


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    state: AccountUiState,
    onEvent: (event: AccountEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.user != null){
                            onNavigateBack()
                        } else {
                          onNavigateToLogin()
                        }

                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            Text(
                text = stringResource(
                    Res.string.settings_version_footer,
                    stringResource(Res.string.app_name),
                    BuildKonfig.appVersionName,
                    BuildKonfig.appVersionCode,
                ),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                hostState = snackbarHostState,
                snackbar = { data ->
                    // Only the error snackbar has an action; the restart one dismisses itself
                    val actionLabel = data.visuals.actionLabel
                    Snackbar(
                        action = actionLabel?.let {
                            {
                                TextButton(
                                    onClick = {
                                        data.dismiss()
                                        onEvent(AccountEvent.ErrorMessageDismissed)
                                    }
                                ) {
                                    Text(actionLabel)
                                }
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

            val okLabel = stringResource(Res.string.ok)
            LaunchedEffect(state.errorMessage) {
                if (state.errorMessage != null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = state.errorMessage,
                            actionLabel = okLabel,
                            withDismissAction = false,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
            }


            val restartRequiredMessage = stringResource(Res.string.settings_restart_required)
            LaunchedEffect(state.showRestartRequiredMessage) {
                if (state.showRestartRequiredMessage) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = restartRequiredMessage,
                            withDismissAction = false,
                            duration = SnackbarDuration.Short
                        )
                        onEvent(AccountEvent.RestartMessageDismissed)
                    }
                }
            }

            LaunchedEffect(state.loggedOut) {
                if (state.loggedOut) onNavigateToLogin()
            }

            if (state.loading) {
                CircularProgressIndicator()
            } else if(state.user != null) {
                UserProfileCard(
                    user = state.user
                )
                SpinnerSettingsRow(
                    icon = { Icon(imageVector = Icons.Default.Language, contentDescription = null) },
                    label = stringResource(Res.string.settings_language),
                    options = AppLanguage.entries,
                    selected = state.selectedLanguage,
                    optionText = { languageDisplayText(it) },
                    onOptionSelected = { onEvent(AccountEvent.LanguageSelected(it)) },
                )
                SpinnerSettingsRow(
                    icon = { Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null) },
                    label = stringResource(Res.string.settings_currency),
                    options = AppCurrency.entries,
                    selected = state.selectedCurrency,
                    optionText = { currencyDisplayText(it) },
                    onOptionSelected = { onEvent(AccountEvent.CurrencySelected(it)) },
                )
                Spacer(modifier = Modifier.weight(1f))
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = stringResource(Res.string.logout),
                    onClick = { onEvent(AccountEvent.LogoutRequested) },
                )
            } else {
                val displayedStatus = state.singInStatus.ifBlank { stringResource(Res.string.not_authenticated) }
                Text(
                    modifier = Modifier.clickable(
                        onClick = { clipboardManager.setText(AnnotatedString(displayedStatus)) }
                    ),
                    text = displayedStatus
                )
                Spacer(modifier.height(30.dp))
                Button(onClick = { onEvent(AccountEvent.SignInWithGoogle)}) {
                    Text(stringResource(Res.string.sign_in_with_google))
                }
            }
        }
    }

    LogoutDialogHost(
        dialog = state.logoutDialog,
        onConfirm = { onEvent(AccountEvent.LogoutConfirmed) },
        onForceLogout = { onEvent(AccountEvent.ForceLogoutConfirmed) },
        onDismiss = { onEvent(AccountEvent.LogoutDialogDismissed) },
    )
}


@Composable
private fun languageDisplayText(language: AppLanguage): String {
    val flag = when (language) {
        AppLanguage.ENGLISH -> "🇺🇸"
        AppLanguage.PORTUGUESE -> "🇧🇷"
    }
    val name = when (language) {
        AppLanguage.ENGLISH -> stringResource(Res.string.settings_language_english)
        AppLanguage.PORTUGUESE -> stringResource(Res.string.settings_language_portuguese)
    }
    return "$flag  $name"
}

@Composable
private fun currencyDisplayText(currency: AppCurrency): String {
    val flag = when (currency) {
        AppCurrency.USD -> "🇺🇸"
        AppCurrency.BRL -> "🇧🇷"
    }
    val name = when (currency) {
        AppCurrency.USD -> stringResource(Res.string.settings_currency_usd)
        AppCurrency.BRL -> stringResource(Res.string.settings_currency_brl)
    }
    return "$flag  $name"
}

@Composable
fun UserProfileCard(user: User, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.picture,
            contentDescription = stringResource(Res.string.profile_picture),
            modifier = Modifier.size(100.dp).aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.email, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview
@Composable
internal fun AccountScreen_Preview() {
    AccountScreen(
        state = AccountUiState(),
        onEvent = {},
        onNavigateBack = {},
        onNavigateToLogin = {}
    )
}

@Preview
@Composable
internal fun AccountScreen_LoggedInPreview() {
    AccountScreen(
        state = AccountUiState(
            user = User(
                name = "Jane Doe",
                email = "jane.doe@email.com",
                picture = "",
                accessToken = "",
                refreshToken = "",
            ),
            selectedLanguage = AppLanguage.PORTUGUESE,
            selectedCurrency = AppCurrency.BRL,
        ),
        onEvent = {},
        onNavigateBack = {},
        onNavigateToLogin = {}
    )
}

@Preview
@Composable
internal fun AccountScreen_LoadingPreview() {
    AccountScreen(
        state = AccountUiState(loading = true),
        onEvent = {},
        onNavigateBack = {},
        onNavigateToLogin = {}
    )
}
