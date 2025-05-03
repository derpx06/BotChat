package com.example.botchat.api

import com.example.botchat.data.OpenRouterModel
import com.example.botchat.data.OpenRouterRequest
import com.example.botchat.data.OpenRouterResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface OpenRouterApiService {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") apiKey: String,
        @Header("HTTP-Referer") referer: String = "http://my_app.com",
        @Header("x-Title") title: String = "My App",
        @Body request: OpenRouterRequest
    ): Response<OpenRouterResponse>
    @GET("models")
    suspend fun getAvailableModels(
        @Header("Authorization") apiKey: String,
        @Header("HTTP-Referer") referer: String = "http://my_app.com",
        @Header("x-Title") title: String = "My App"
    ): Response<List<OpenRouterModel>>
    companion object{
        const val BASE_URL = "https://api.openrouter.ai/v1/"
        fun create(): OpenRouterApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenRouterApiService::class.java)
        }
    }
}