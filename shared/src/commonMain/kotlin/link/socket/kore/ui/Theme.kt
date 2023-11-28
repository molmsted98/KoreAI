package link.socket.kore.ui

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val backgroundColor = Color(0xFFF2F2F7)
val bodyTextColor = Color(0xFF575758)
val nonContentCardColor = Color(0xFFF8F8F8)
val iconAlpha = 0.5f
val separatorIconSize = 4.dp
val iconButtonSize = 48.dp

@Composable
fun themeColors() = MaterialTheme.colors.copy(
    background = backgroundColor,
)

@Composable
fun themeTypography() = with(MaterialTheme.typography) {
    copy(
        body1 = body1.copy(color = bodyTextColor),
        body2 = body2.copy(color = bodyTextColor),
    )
}

@Composable
fun themeShapes() = with(MaterialTheme.shapes) {
    copy(
        small = small.copy(CornerSize(4.dp)),
    )
}
