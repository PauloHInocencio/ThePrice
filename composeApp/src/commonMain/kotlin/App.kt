import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import br.com.noartcode.theprice.ui.navigation.AppNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext


@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            AppNavGraph()
        }
    }
}
