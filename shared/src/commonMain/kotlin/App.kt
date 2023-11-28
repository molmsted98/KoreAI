import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import link.kore.shared.config.KotlinConfig
import link.socket.kore.model.agent.example.WeatherAgent
import kotlin.time.Duration.Companion.seconds

val openAI = OpenAI(
    token = KotlinConfig.openai_api_key,
    timeout = Timeout(socket = 45.seconds),
    logging = LoggingConfig(logLevel = LogLevel.None),
)
val agent = WeatherAgent(openAI)

@Composable
fun App() {
    MaterialTheme {
        var response by remember { mutableStateOf(emptyList<String>()) }

        LaunchedEffect(Unit) {
            with(agent) {
                initialize()

                var rerun: Boolean

                do {
                    response = getChatHistoryStrings()
                    rerun = execute()
                } while (rerun)

                response = getChatHistoryStrings()
                logChatHistory()
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn {
                items(response) { chat ->
                    Text(chat)
                }
            }
        }
    }
}
