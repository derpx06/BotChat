package com.example.ChatBlaze.data.downlaod

enum class DownloadStatus {
    NOT_DOWNLOADED, DOWNLOADING, DOWNLOADED, FAILED
}

data class DownloadableModel(
    val id: String,
    val name: String,
    val url: String,
    val description: String,
    val size: String,
    val status: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val progress: Int = 0
)