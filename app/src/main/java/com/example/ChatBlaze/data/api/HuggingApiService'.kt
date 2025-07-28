package com.example.ChatBlaze.data.api

import com.example.ChatBlaze.data.model.HuggingFaceRequest
import com.example.ChatBlaze.data.model.HuggingFaceResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface HuggingFaceApiService {
    @POST
    suspend fun query(
        @Header("Authorization") authorization: String,
        @Body payload: HuggingFaceRequest,
        @Url url: String
    ): Response<List<HuggingFaceResponse>>

    companion object {
        private const val BASE_URL = "https://api-inference.huggingface.co/"

        fun create(): HuggingFaceApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Log request/response
            }

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HuggingFaceApiService::class.java)
        }
    }
}