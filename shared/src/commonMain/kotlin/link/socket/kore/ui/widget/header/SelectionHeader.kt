package link.socket.kore.ui.widget.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.theme.themeTypography

@Composable
fun SelectionHeader(
    modifier: Modifier = Modifier,
    selectionConfig: SelectionConfig,
) {
    with(selectionConfig) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                style = themeTypography().h6,
                text = firstOption,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.requiredWidth(16.dp))

            Text(
                style = themeTypography().caption,
                text = "or",
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.requiredWidth(8.dp))

            TextButton(
                onClick = onSecondOptionSelected
            ) {
                Text(
                    style = themeTypography().button,
                    text = secondOption,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
