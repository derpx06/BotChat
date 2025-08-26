package com.example.ChatBlaze.data.downlaod

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DownloadRepository {
    fun downloadModel(model: Model): UUID
    fun cancelDownload(model: Model)
    fun getDownloadProgress(workId: UUID): Flow<WorkInfo>
}

class DefaultDownloadRepository(context: Context) : DownloadRepository {
    private val workManager = WorkManager.getInstance(context)

    override fun downloadModel(model: Model): UUID {
        val inputData = workDataOf(
            KEY_MODEL_URL to model.url,
            KEY_MODEL_ID to model.id
        )

        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(model.id)
            .build()

        workManager.enqueueUniqueWork(
            model.id,
            ExistingWorkPolicy.KEEP,
            downloadWorkRequest
        )

        return downloadWorkRequest.id
    }

    override fun cancelDownload(model: Model) {
        workManager.cancelUniqueWork(model.id)
    }

    override fun getDownloadProgress(workId: UUID): Flow<WorkInfo> {
        return workManager.getWorkInfoByIdFlow(workId) as Flow<WorkInfo>
    }
}