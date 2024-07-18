import androidx.compose.ui.window.ComposeUIViewController
import br.com.noartcode.theprice.ui.di.KoinInitializer

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
    }
) { App() }