import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.com.noartcode.theprice.ui.di.KoinInitializer
import androidx.compose.runtime.*
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.imageio.ImageIO

private val koin = KoinInitializer().init()

fun main() = application {

    var showWindow by remember { mutableStateOf(false) }

    // Create a hidden AWT frame to act as the parent for popupMenu
    val dummyFrame = remember {
        Frame().apply {
            isUndecorated = true
            type = Window.Type.POPUP
            setSize(1, 1)
            setLocation(0, 0)
            isVisible = true
        }
    }


    val popupMenu = remember {
        PopupMenu().apply {
            add(MenuItem("Quit").apply {
                addActionListener { exitApplication() }
            })
        }
    }

    // ✅ Must add the popupMenu to the dummyFrame BEFORE using .show() (I don't like this)
    LaunchedEffect(Unit) {
        dummyFrame.add(popupMenu)
    }


    val image = remember {
        ImageIO.read(object {}.javaClass.getResource("/theprice_icon.png"))
    }

    val trayIcon = remember {
        TrayIcon(image).apply {
            isImageAutoSize = true
            addMouseListener(object : MouseAdapter() {
                override fun mouseReleased(e: MouseEvent) {
                    when (e.button) {
                        MouseEvent.BUTTON1 -> showWindow = !showWindow
                        MouseEvent.BUTTON3 -> {
                            popupMenu.show(dummyFrame,e.xOnScreen, e.yOnScreen) // wired result
                        }
                    }
                }
            })
        }
    }

    DisposableEffect(Unit) {
        if (SystemTray.isSupported()) {
            SystemTray.getSystemTray().add(trayIcon)
            onDispose {
                SystemTray.getSystemTray().remove(trayIcon)
                dummyFrame.dispose()
            }
        }
        onDispose { }
    }

    Window(
        visible = showWindow,
        onCloseRequest = { showWindow = false },
        focusable = true,
        alwaysOnTop = true,
        title = "ThePrice",
    ) {
        App()
    }
}