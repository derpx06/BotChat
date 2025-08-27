package com.example.ChatBlaze.data.downlaod

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException

const val KEY_MODEL_URL = "key_model_url"
const val KEY_MODEL_ID = "key_model_id"
const val KEY_MODEL_NAME = "key_model_name"
const val KEY_MODEL_SIZE = "key_model_size"
const val KEY_PROGRESS = "key_progress"
const val KEY_DOWNLOADED_BYTES = "key_downloaded_bytes"
const val KEY_ERROR_MSG = "key_error_msg"

private const val TAG = "DownloadWorker"

class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val okHttpClient = OkHttpClient()

    override suspend fun doWork(): Result {
        val modelUrl = inputData.getString(KEY_MODEL_URL)
        val modelId = inputData.getString(KEY_MODEL_ID)
        val modelSize = inputData.getLong(KEY_MODEL_SIZE, -1L)

        if (modelUrl == null || modelId == null) {
            return Result.failure(workDataOf(KEY_ERROR_MSG to "Missing model URL or ID"))
        }

        val destinationFile = getDestinationFile(modelId, modelUrl)

        return try {
            downloadFile(modelUrl, destinationFile, modelSize)
            if (isStopped) {
                destinationFile.delete()
                Result.failure()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download failed for model '$modelId'", e)
            destinationFile.delete() // Clean up partial file on failure
            Result.failure(workDataOf(KEY_ERROR_MSG to "Download failed: ${e.message}"))
        }
    }

    private suspend fun downloadFile(url: String, destinationFile: File, totalSize: Long) {
        withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) throw IOException("Server error: ${response.code}")

            val body = response.body ?: throw IOException("Response body is null")
            val source = body.source()

            // Set initial progress
            setProgress(workDataOf(KEY_PROGRESS to 0, KEY_DOWNLOADED_BYTES to 0L))

            destinationFile.sink().buffer().use { sink ->
                val buffer = okio.Buffer()
                var bytesRead = 0L
                var lastProgress = -1
                var lastUpdateTime = System.currentTimeMillis()

                var read: Long
                while (source.read(buffer, 8192L).also { read = it } != -1L) {
                    if (isStopped) {
                        Log.d(TAG, "Worker has been stopped/cancelled.")
                        break
                    }

                    sink.write(buffer, read)
                    bytesRead += read

                    val progress = if (totalSize > 0) ((bytesRead * 100) / totalSize).toInt() else 0

                    val currentTime = System.currentTimeMillis()
                    if (progress != lastProgress || currentTime - lastUpdateTime >= 500) {
                        lastProgress = progress
                        lastUpdateTime = currentTime

                        Log.d(TAG, "Progress: $progress%, Bytes: $bytesRead / $totalSize")
                        setProgress(workDataOf(KEY_PROGRESS to progress, KEY_DOWNLOADED_BYTES to bytesRead))
                    }
                }
            }

            if (!isStopped) {
                val finalBytes = if (totalSize > 0) totalSize else destinationFile.length()
                setProgress(workDataOf(KEY_PROGRESS to 100, KEY_DOWNLOADED_BYTES to finalBytes))
            }
        }
    }

    private fun getDestinationFile(modelId: String, modelUrl: String): File {
        val dir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
        if (!dir.exists()) dir.mkdirs()
        val extension = modelUrl.substringAfterLast('.', "bin")
        return File(dir, "$modelId.$extension")
    }
}