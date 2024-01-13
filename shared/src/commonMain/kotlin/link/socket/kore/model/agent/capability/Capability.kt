package link.socket.kore.model.agent.capability

import link.socket.kore.model.tool.FunctionProvider

interface Capability {
    val impl: Pair<String, FunctionProvider>
}