import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import br.com.noartcode.theprice.data.remote.workers.ISyncEventsWorker
import br.com.noartcode.theprice.ui.navigation.AppNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject


@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val worker = koinInject<ISyncEventsWorker>()

            LaunchedEffect(Unit){
                worker.start()
            }

            DisposableEffect(Unit){
                onDispose {
                    worker.stop()
                }
            }

            AppNavGraph()
        }
    }
}
