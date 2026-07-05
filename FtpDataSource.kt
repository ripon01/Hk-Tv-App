package com.example.ftpplayer

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.IOException
import java.io.InputStream

/**
 * Streams a single file from an FTP server (anonymous login) directly into ExoPlayer,
 * without downloading it to disk first.
 */
class FtpDataSource(private val host: String) : BaseDataSource(/* isNetwork= */ true) {

    private var ftpClient: FTPClient? = null
    private var inputStream: InputStream? = null
    private var dataSpecUri: Uri? = null
    private var bytesRemaining: Long = 0

    override fun open(dataSpec: DataSpec): Long {
        dataSpecUri = dataSpec.uri
        val path = dataSpec.uri.path ?: throw IOException("Invalid FTP path: ${dataSpec.uri}")

        transferInitializing(dataSpec)

        val client = FTPClient()
        try {
            client.connectTimeout = 8000
            client.connect(host)
            if (!client.login("anonymous", "anonymous@")) {
                throw IOException("Anonymous FTP login failed")
            }
            client.enterLocalPassiveMode()
            client.setFileType(FTP.BINARY_FILE_TYPE)

            if (dataSpec.position > 0) {
                client.setRestartOffset(dataSpec.position)
            }

            val stream = client.retrieveFileStream(path)
                ?: throw IOException("FTP server could not open file: $path")

            ftpClient = client
            inputStream = stream
        } catch (e: IOException) {
            try {
                client.disconnect()
            } catch (_: IOException) {
            }
            throw e
        }

        bytesRemaining = if (dataSpec.length != C.LENGTH_UNSET.toLong()) {
            dataSpec.length
        } else {
            C.LENGTH_UNSET.toLong()
        }

        transferStarted(dataSpec)
        return bytesRemaining
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        val stream = inputStream ?: throw IOException("DataSource not opened")

        val bytesRead = stream.read(buffer, offset, length)
        if (bytesRead == -1) {
            return C.RESULT_END_OF_INPUT
        }
        bytesTransferred(bytesRead)
        if (bytesRemaining != C.LENGTH_UNSET.toLong()) {
            bytesRemaining -= bytesRead
        }
        return bytesRead
    }

    override fun getUri(): Uri? = dataSpecUri

    override fun close() {
        try {
            inputStream?.close()
        } catch (_: IOException) {
        } finally {
            inputStream = null
            val client = ftpClient
            ftpClient = null
            if (client != null) {
                try {
                    client.completePendingCommand()
                } catch (_: IOException) {
                }
                try {
                    client.logout()
                } catch (_: IOException) {
                }
                try {
                    client.disconnect()
                } catch (_: IOException) {
                }
            }
            transferEnded()
        }
    }

    class Factory(private val host: String) : DataSource.Factory {
        override fun createDataSource(): DataSource = FtpDataSource(host)
    }
}
