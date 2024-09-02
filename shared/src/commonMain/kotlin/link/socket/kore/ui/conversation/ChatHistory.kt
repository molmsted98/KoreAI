package link.socket.kore.ui.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.core.Role
import link.socket.kore.model.chat.Chat
import link.socket.kore.ui.theme.themeColors

/**
 * A composable function that displays the chat history in a lazy column.
 *
 * This function takes a list of chat messages and displays them in a scrollable column.
 * Each message is displayed using the ChatMessage composable. If the chat is loading,
 * a circular progress indicator is shown at the bottom of the list.
 *
 * @param modifier Modifier to be applied to the LazyColumn.
 * @param listState State of the lazy list to control scrolling.
 * @param messages List of chat messages to be displayed.
 * @param isLoading Boolean flag to indicate if the chat is loading.
 * @param displaySnackbar Function to display a snackbar with a given message.
 */
@Composable
fun ChatHistory(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    messages: List<Chat>,
    isLoading: Boolean,
    displaySnackbar: (String) -> Unit,
    showSystemMessages: Boolean = true,
    backgroundColor: Color = themeColors().surface,
    textColor: Color = MaterialTheme.colors.onBackground,
    codeTextColor: Color = MaterialTheme.colors.onBackground,
    linkTextColor: Color = textColor,
    codeBackgroundColor: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
    inlineCodeBackgroundColor: Color = codeBackgroundColor,
    dividerColorColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 16.dp,
            end = 8.dp,
            bottom = 72.dp,
        ),
    ) {
        itemsIndexed(
            messages.filter { showSystemMessages || it.role != Role.System }
        ) { index, message ->
            ChatMessage(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                message = message,
                displaySnackbar = displaySnackbar,
                showRegenerate = (index == messages.size - 1) && message.role == Role.Assistant,
                backgroundColor = backgroundColor,
                textColor = textColor,
                codeTextColor = codeTextColor,
                linkTextColor = linkTextColor,
                codeBackgroundColor = codeBackgroundColor,
                inlineCodeBackgroundColor = inlineCodeBackgroundColor,
                dividerColor = dividerColorColor,
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
