package com.example.ChatBlaze.data.downlaod

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

const val CHANNEL_ID = "download_channel"
const val NOTIFICATION_ID = 1
const val KEY_MODEL_URL = "model_url"
const val KEY_MODEL_ID = "model_id"
const val KEY_PROGRESS = "progress"
const val KEY_ERROR_MSG = "error_msg"

class DownloadWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val urlString = inputData.getString(KEY_MODEL_URL) ?: return Result.failure()
        val modelId = inputData.getString(KEY_MODEL_ID) ?: return Result.failure()
        val file = getDestinationFile(applicationContext, modelId)

        createNotificationChannel(applicationContext)

        var result: Result = Result.failure()

        try {
            withContext(Dispatchers.IO) {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                val existingLength = file.length()
                if (existingLength > 0) {
                    connection.setRequestProperty("Range", "bytes=$existingLength-")
                }
                connection.connect()

                val fileLength = connection.contentLength + existingLength
                val input = connection.inputStream
                val output = FileOutputStream(file, existingLength > 0)
                val data = ByteArray(4096)
                var total = existingLength
                var count: Int

                while (input.read(data).also { count = it } != -1) {
                    if (isStopped) {
                        output.close()
                        input.close()
                        file.delete()
                        result = Result.failure()
                        return@withContext
                    }
                    total += count
                    output.write(data, 0, count)
                    val progress = (total * 100 / fileLength).toInt()
                    setProgress(workDataOf(KEY_PROGRESS to progress))
                    updateNotification(progress, modelId)
                }

                output.flush()
                output.close()
                input.close()

                if (file.length() != fileLength) {
                    file.delete()
                    result = Result.failure(workDataOf(KEY_ERROR_MSG to "Incomplete download"))
                } else {
                    result = Result.success()
                }
            }
            return result
        } catch (e: Exception) {
            file.delete()
            return Result.failure(workDataOf(KEY_ERROR_MSG to e.message))
        } finally {
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag(modelId)
        }
    }

    private fun getDestinationFile(context: Context, modelId: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
        return File(dir, "$modelId.tflite")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Model Downloads", NotificationManager.IMPORTANCE_LOW)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun updateNotification(progress: Int, modelId: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Downloading $modelId")
            .setContentText("$progress% complete")
            .setSmallIcon(R.drawable.stat_sys_download)
            .setProgress(100, progress, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID + modelId.hashCode(), notification)
    }
}