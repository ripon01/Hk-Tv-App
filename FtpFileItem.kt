package com.example.ftpplayer

data class FtpFileItem(
    val name: String,
    val fullPath: String,
    val isDirectory: Boolean,
    val size: Long
) {
    fun isVideoFile(): Boolean {
        val lower = name.lowercase()
        return VIDEO_EXTENSIONS.any { lower.endsWith(it) }
    }

    companion object {
        val VIDEO_EXTENSIONS = listOf(
            ".mp4", ".mkv", ".avi", ".mov", ".webm", ".flv", ".ts", ".m4v", ".3gp"
        )
    }
}
