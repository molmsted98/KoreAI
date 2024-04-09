package link.socket.kore.io

actual fun createFile(folderPath: String, fileName: String, fileContent: String): Result<String> =
    Result.failure(UnsupportedOperationException())

actual  fun parseCsv(folderPath: String, fileName: String): Result<List<List<String>>> =
    Result.failure(UnsupportedOperationException())

actual fun readFile(folderPath: String, fileName: String): Result<String> =
    Result.failure(UnsupportedOperationException())
