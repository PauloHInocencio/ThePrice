import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import br.com.noartcode.theprice.data.remote.networking.SessionEvents
import br.com.noartcode.theprice.data.remote.workers.ISyncEventsWorker
import br.com.noartcode.theprice.domain.usecases.user.IClearUserSession
import br.com.noartcode.theprice.ui.navigation.AppNavGraph
import br.com.noartcode.theprice.ui.presentation.login.navigateToLogin
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject


@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val worker = koinInject<ISyncEventsWorker>()
            val sessionEvents = koinInject<SessionEvents>()
            val clearUserSession = koinInject<IClearUserSession>()
            val navController = rememberNavController()

            LaunchedEffect(Unit){
                worker.start()
            }

            // Refresh token rejected: wipe the session and send the user to Login
            LaunchedEffect(Unit) {
                sessionEvents.expired.collect {
                    clearUserSession()
                    navController.navigateToLogin(sessionExpired = true)
                }
            }

            DisposableEffect(Unit){
                onDispose {
                    worker.stop()
                }
            }

            AppNavGraph(navController = navController)
        }
    }
}
