package com.example.ChatBlaze.data.downlaod
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class ModelDownloader(private val context: Context) {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    fun downloadModel(model: Model): Long {
        val request = DownloadManager.Request(model.url.toUri())
            .setMimeType("application/octet-stream")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setTitle("Downloading: ${model.name}")
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOCUMENTS, "${model.id}.tflite")

        return downloadManager.enqueue(request)
    }
}