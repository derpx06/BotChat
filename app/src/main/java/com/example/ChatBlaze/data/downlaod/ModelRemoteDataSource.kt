package com.example.ChatBlaze.data.downlaod

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ModelRemoteDataSource(private val context: Context) {

    private val gson = Gson()

    suspend fun getModels(): List<Model> {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString = context.assets.open("models.json").bufferedReader().use { it.readText() }
                gson.fromJson(jsonString, ModelList::class.java).models
            } catch (ioException: IOException) {
                emptyList()
            }
        }
    }
}