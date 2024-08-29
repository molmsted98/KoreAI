package link.socket.kore.io

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile
import platform.darwin.NSObject
import platform.darwin.NSObjectMeta
import kotlin.error

private class BundleMarker : NSObject() {
    companion object : NSObjectMeta()
}

@OptIn(BetaInteropApi::class)
val bundle by lazy {
    NSBundle.bundleForClass(BundleMarker)
}

actual fun readFolderContents(folderPath: String): Result<List<String>> =
    Result.failure(UnsupportedOperationException())

actual fun createFile(folderPath: String, fileName: String, fileContent: String): Result<String> =
    Result.failure(UnsupportedOperationException())

actual fun parseCsv(folderPath: String, fileName: String): Result<List<List<String>>> =
    Result.failure(UnsupportedOperationException())

@OptIn(ExperimentalForeignApi::class)
actual fun readFile(folderPath: String, fileName: String): Result<String> {

    val (filename, type) = when (val lastPeriodIndex = fileName.lastIndexOf('.')) {
        0 -> {
            null to fileName.drop(1)
        }

        in 1..Int.MAX_VALUE -> {
            fileName.take(lastPeriodIndex) to fileName.drop(lastPeriodIndex + 1)
        }

        else -> {
            fileName to null
        }
    }
    val path = bundle.pathForResource(filename, type) ?: error(
        "Couldn't get path of $fileName (parsed as: ${
            listOfNotNull(
                filename,
                type
            ).joinToString(".")
        })"
    )

    return memScoped {
        NSString.stringWithContentsOfFile(
            path = path,
            encoding = NSUTF8StringEncoding,
            error = null,
        )?.let { contents ->
            Result.success(contents)
        } ?: Result.failure(
            RuntimeException("Couldn't load resource: $fileName")
        )
    }
}
