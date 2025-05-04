package com.example.botchat.api

import com.example.botchat.data.OpenRouterRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import android.util.Log

interface OpenRouterApiService {
    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: OpenRouterRequest
    ): Map<String, Any>

    companion object {
        fun create(): OpenRouterApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://openrouter.ai/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(OpenRouterApiService::class.java)
        }
    }
}