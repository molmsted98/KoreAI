package link.socket.kore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AddCircleOutline
import androidx.compose.material.icons.twotone.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun ChatTextEntry(
    modifier: Modifier = Modifier,
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = 8.dp,
    ) {
        Row(
            modifier = modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                modifier = Modifier
                    .requiredSize(iconButtonSize),
                onClick = {
                    // TODO: Handle click
                }
            ) {
                Image(
                    imageVector = Icons.TwoTone.AddCircleOutline,
                    alpha = iconAlpha,
                    contentDescription = "Expands attachment buttons",
                )
            }

            Spacer(modifier = Modifier.requiredWidth(8.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth(.90f),
                value = textFieldValue,
                onValueChange = onTextChanged,
                label = { Text("Your message...") },
            )

            Spacer(modifier = Modifier.requiredWidth(8.dp))

            IconButton(
                modifier = Modifier
                    .requiredSize(iconButtonSize),
                onClick = {
                    // TODO: Handle click
                }
            ) {
                Image(
                    imageVector = Icons.TwoTone.ArrowUpward,
                    alpha = iconAlpha,
                    contentDescription = "Sends the current message",
                )
            }
        }
    }
}
