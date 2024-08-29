package link.socket.kore.model.app

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.kore.shared.config.KotlinConfig
import link.socket.kore.data.ConversationRepository
import kotlin.time.Duration.Companion.hours

class Application(
    scope: CoroutineScope,
) {
    val conversationRepository = ConversationRepository(scope)

    val openAI = OpenAI(
        token = KotlinConfig.openai_api_key,
        timeout = Timeout(socket = 1.hours),
        logging = LoggingConfig(
            logLevel = LogLevel.Body,
            logger = Logger.Simple,
        ),
    )
}