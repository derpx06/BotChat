package com.example.ChatBlaze.api

import com.example.ChatBlaze.data.GeminiRequest
import com.example.ChatBlaze.data.GeminiResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("models/gemini-1.5-pro:generateContent")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

        fun create(): GeminiApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(GeminiApiService::class.java)
        }
    }
}