package link.socket.kore.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.theme.themeTypography

@Composable
fun AgentCard(
    modifier: Modifier = Modifier,
    agent: KoreAgent,
) {
    Surface(
        modifier = modifier
            .requiredSize(64.dp),
        elevation = 2.dp,
    ) {
        Column {
            Text(
                style = themeTypography().h6,
                text = agent.name,
            )
        }
    }
}
