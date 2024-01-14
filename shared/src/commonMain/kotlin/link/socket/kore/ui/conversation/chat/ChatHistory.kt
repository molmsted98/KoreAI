package link.socket.kore.ui.conversation.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.core.Role
import link.socket.kore.model.conversation.KoreMessage

@Composable
fun ChatHistory(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    messages: List<KoreMessage>,
    isLoading: Boolean,
    displaySnackbar: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(8.dp),
    ) {
        itemsIndexed(messages) { index, message ->
            ChatMessage(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                message = message,
                displaySnackbar = displaySnackbar,
                showRegenerate = (index == messages.size - 1) && message.role == Role.Assistant,
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
