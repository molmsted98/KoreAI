package link.socket.kore.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.widget.header.SelectionConfig
import link.socket.kore.ui.widget.header.SelectionHeader

@Composable
fun HomeHeader(
    modifier: Modifier = Modifier,
    selectionEnabled: Boolean,
    drawerExpanded: Boolean,
    onExpandDrawer: () -> Unit,
) {
    Surface(
        modifier = modifier,
        elevation = 16.dp,
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
        ) {
            SelectionHeader(
                selectionConfig = SelectionConfig(
                    selectionEnabled = selectionEnabled,
                    selectedTitle = "",
                    firstOption = "Start a Conversation",
                    secondOption = "Browse All Agents",
                    onSecondOptionSelected = {
                        // TODO: Navigation to Agent list
                    }
                ),
            )
        }
    }
}
