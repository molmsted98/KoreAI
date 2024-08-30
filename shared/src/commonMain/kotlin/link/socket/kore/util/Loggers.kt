package link.socket.kore.util

import co.touchlab.kermit.Logger

private const val PLATFORM = "kore-ai"

internal fun logWith(tag: String?): Logger =
    Logger.withTag("$PLATFORM/$tag")
