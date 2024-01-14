package link.socket.kore

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.kore.shared.config.KotlinConfig
import link.socket.kore.data.ConversationRepository
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.bundled.*
import kotlin.time.Duration.Companion.hours

class Application(
    scope: CoroutineScope,
) {

    val conversationRepository = ConversationRepository(scope)

    val openAI = OpenAI(
        token = KotlinConfig.openai_api_key,
        timeout = Timeout(socket = 1.hours),
        logging = LoggingConfig(logLevel = LogLevel.All),
    )

    val agentList: List<KoreAgent> = listOf(
        DefineAgentAgent(conversationRepository, openAI, scope),
        DelegateTasksAgent(conversationRepository, openAI, scope),
        LocalCapabilitiesAgent(conversationRepository, openAI, scope),
        WriteCodeAgent(conversationRepository, openAI, scope),
        ModifyFileAgent(conversationRepository, openAI, scope),
        CleanJsonAgent(conversationRepository, openAI, scope),
        FinancialAgent(conversationRepository, openAI, scope),
    )
}