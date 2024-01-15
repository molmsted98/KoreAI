package link.socket.kore

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import link.socket.kore.ui.App

fun MainViewController() =
    ComposeUIViewController {
        App(
            modifier = Modifier
                .padding(top = 64.dp),
        )
    }
