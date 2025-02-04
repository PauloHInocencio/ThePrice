package br.com.noartcode.theprice.ui.presentation.auth.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil3.compose.rememberAsyncImagePainter


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
            if(state.user != null) {
                UserProfileCard(
                    user = state.user
                )
                Button(onClick = { }) {
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
        ProfilePicture(user.picture)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.email, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ProfilePicture(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Profile Picture",
        modifier = Modifier.size(100.dp).aspectRatio(1f),
        contentScale = ContentScale.Crop
    )
}