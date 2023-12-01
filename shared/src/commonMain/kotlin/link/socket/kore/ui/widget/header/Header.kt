package link.socket.kore.ui.widget.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.theme.themeTypography

data class SelectionConfig(
    val selectionEnabled: Boolean,
    val selectedTitle: String,
    val firstOption: String,
    val secondOption: String,
    val onSecondOptionSelected: () -> Unit,
)

@Composable
fun Header(
    modifier: Modifier = Modifier,
    selectionConfig: SelectionConfig?,
    displayBackIcon: Boolean,
    displayMenuIcon: Boolean,
    drawerExpanded: Boolean,
    onExpandDrawer: () -> Unit,
) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (displayBackIcon) {
            IconButton(
                modifier = Modifier
                    .requiredSize(64.dp),
                onClick = {
                    // TODO: Handle back press
                }
            ) {
                Image(
                    imageVector = Icons.TwoTone.ArrowBack,
                    contentDescription = "Back",
                )
            }
        } else {
            Spacer(modifier = Modifier.requiredWidth(64.dp))
        }

        selectionConfig?.apply {
            if (selectionEnabled) {
                if (drawerExpanded) {
                    SelectionHeader(selectionConfig = this)
                } else {
                    Text(
                        style = themeTypography().h6,
                        text = firstOption,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Text(
                    style = themeTypography().h6,
                    text = selectedTitle,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (displayMenuIcon) {
            IconButton(
                modifier = Modifier
                    .requiredSize(64.dp),
                onClick = onExpandDrawer,
            ) {
                Image(
                    imageVector = Icons.TwoTone.Menu,
                    contentDescription = "Menu",
                )
            }
        } else {
            Spacer(modifier = Modifier.requiredWidth(64.dp))
        }
    }
}
