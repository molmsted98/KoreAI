package link.socket.kore.model.capability

import link.socket.kore.model.tool.FunctionProvider

interface Capability {

    val tag: String
        get() = "Capability"

    val impl: Pair<String, FunctionProvider>
}