package link.socket.kore.io

expect fun createFile(folderPath: String, fileName: String, fileContent: String): Result<String>
expect fun parseCsv(folderPath: String, fileName: String): Result<List<List<String>>>
expect fun readFile(folderPath: String, fileName: String): Result<String>
