package link.socket.kore

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.App

@Composable fun MainView() =
    App(
        modifier = Modifier
            .padding(top = 64.dp),
    )
