package com.example.botchat.api

import com.example.botchat.data.ChatRequest
import com.example.botchat.data.ChatResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ChatApiService {
    @POST("api/chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): Response<ChatResponse> // Use Response wrapper for error handling

    companion object {
        private const val BASE_URL = "https://lung-heights-gbp-why.trycloudflare.com/"

        fun create(): ChatApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Changed to BODY for more detailed logs
            }

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChatApiService::class.java)
        }
    }
}