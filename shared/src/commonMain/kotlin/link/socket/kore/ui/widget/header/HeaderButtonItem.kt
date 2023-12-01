package link.socket.kore.ui.widget.header

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.theme.themeTypography

@Composable
fun HeaderButtonItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = modifier
            .padding(end = 8.dp),
        onClick = onClick,
    ) {
        Text(
            style = themeTypography().button,
            text = text,
        )
    }
}
