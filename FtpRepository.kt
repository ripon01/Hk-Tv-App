package com.example.ftpplayer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import java.io.IOException

/**
 * Talks to the FTP server anonymously and lists directory contents.
 * All network calls run on Dispatchers.IO.
 */
class FtpRepository(private val host: String) {

    private fun newConnectedClient(): FTPClient {
        val client = FTPClient()
        client.connectTimeout = 8000
        client.connect(host)
        val reply = client.replyCode
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect()
            throw IOException("FTP server refused connection ($host)")
        }
        val loggedIn = client.login("anonymous", "anonymous@")
        if (!loggedIn) {
            throw IOException("Anonymous login failed")
        }
        client.enterLocalPassiveMode()
        client.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)
        return client
    }

    /**
     * Lists files/folders at [path] (e.g. "/" or "/Movies").
     */
    suspend fun listDirectory(path: String): List<FtpFileItem> = withContext(Dispatchers.IO) {
        val client = newConnectedClient()
        try {
            val files = client.listFiles(path)
            files
                .filter { it.name != "." && it.name != ".." }
                .map { f ->
                    val childPath = if (path.endsWith("/")) "$path${f.name}" else "$path/${f.name}"
                    FtpFileItem(
                        name = f.name,
                        fullPath = childPath,
                        isDirectory = f.isDirectory,
                        size = f.size
                    )
                }
                .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        } finally {
            try {
                client.logout()
                client.disconnect()
            } catch (_: IOException) {
            }
        }
    }
}
