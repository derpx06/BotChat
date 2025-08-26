package com.example.ChatBlaze.data.downlaod

enum class ModelDownloadStatusType {
    NOT_DOWNLOADED, IN_PROGRESS, SUCCEEDED, FAILED, CANCELLED
}

data class ModelDownloadStatus(
    val status: ModelDownloadStatusType,
    val progress: Int = 0,
    val errorMessage: String = ""
)

data class Model(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val size: String,
    val version: String,
    val downloadStatus: ModelDownloadStatus = ModelDownloadStatus(ModelDownloadStatusType.NOT_DOWNLOADED)
)
