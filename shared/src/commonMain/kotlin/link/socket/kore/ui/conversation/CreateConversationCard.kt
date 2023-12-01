package link.socket.kore.ui.conversation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.theme.createCardHeight
import link.socket.kore.ui.theme.themeTypography

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateConversationCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(createCardHeight),
        elevation = 2.dp,
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                style = themeTypography().h4,
                text = "+",
                textAlign = TextAlign.Center,
            )
        }
    }
}
