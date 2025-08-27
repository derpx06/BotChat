package com.example.ChatBlaze.data.downlaod

import java.util.UUID

enum class DownloadStatus {
    NOT_DOWNLOADED, ENQUEUED, DOWNLOADING, DOWNLOADED, FAILED
}

data class Model(
    val id: String,
    val name: String,
    val url: String,
    val description: String,
    val size: Long,
    val version: String,
    val category: String,
    val architecture: String,
    val status: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val progress: Int = 0,
    val downloadedMegabytes: Long = 0,
    val workId: UUID? = null
)

data class ModelUiState(
    val models: List<Model> = emptyList(),
    val isLoading: Boolean = true,
    val errorToShow: String? = null
)
data class ModelList(
    val models: List<Model>
)


