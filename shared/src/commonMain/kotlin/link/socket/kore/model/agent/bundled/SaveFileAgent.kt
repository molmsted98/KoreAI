package link.socket.kore.model.agent.bundled

import link.socket.kore.model.agent.KoreAgent

data class SaveFileAgent(
    val filepath: String,
    val fileContent: String,
) : KoreAgent.Unassisted {

    // override suspend fun execute(): String? {
    //     // TODO: Save the file
    //     return ""
    // }
}
