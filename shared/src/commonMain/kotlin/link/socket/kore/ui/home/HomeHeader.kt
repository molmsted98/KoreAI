package link.socket.kore.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.widget.header.Header

/**
 * A composable function that displays the header for the home screen.
 *
 * This function creates a header with a title "Agent Conversations" and a home button.
 * The home button does not display a back icon and does not perform any action when clicked.
 *
 * @param modifier A [Modifier] for this composable. Defaults to [Modifier].
 *                 This can be used to adjust the layout or appearance of the header.
 */
@Composable
fun HomeHeader(
    modifier: Modifier = Modifier,
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
            Header(
                title = "Agent Conversations",
                displayBackIcon = false,
                onBackClicked = {},
            )
        }
    }
}
