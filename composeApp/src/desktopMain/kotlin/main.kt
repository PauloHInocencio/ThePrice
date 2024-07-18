import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.awaitApplication
import br.com.noartcode.theprice.ui.di.KoinInitializer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = application {
    KoinInitializer().init()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ThePrice",
    ) {
        App()
    }
}