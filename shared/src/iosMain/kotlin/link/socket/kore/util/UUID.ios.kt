package link.socket.kore.util

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
