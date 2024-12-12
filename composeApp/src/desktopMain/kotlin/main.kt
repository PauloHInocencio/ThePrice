import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.com.noartcode.theprice.ui.di.KoinInitializer

fun main() = application {
    KoinInitializer().init()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ThePrice",
    ) {
        App()
    }
}