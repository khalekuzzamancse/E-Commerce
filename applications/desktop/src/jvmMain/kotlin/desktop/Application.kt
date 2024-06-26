package desktop

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ui.LoginFactory
import ui.LoginRoute


fun main() {
    application {
        val state = remember {
            WindowState(
                position = WindowPosition(0.dp, 0.dp),
            )
        }
        state.size = DpSize(width = 400.dp, height = 700.dp)
        Window(
            state = state,
            title = "CMP Template",
            onCloseRequest = ::exitApplication
        ) {
            MaterialTheme {
                val loginController = remember { LoginFactory.createLoginFormController() }
                LoginRoute(
                    controller = loginController,
                    onEvent = {
                        println(it)
                    }
                )
//                val registerController = remember { RegisterFactory.createController() }
//                RegisterRoute(
//                    controller = registerController,
//                    onEvent = {
//                        println(it)
//                    }
//                )
            }
        }
    }

}