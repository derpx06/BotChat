package com.example.ChatBlaze.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Utility object to configure and create HTTP clients and Retrofit instances.
 * Centralizes common configurations like timeouts and logging.
 */
object ApiClientConfig {
    private const val CONNECT_TIMEOUT = 300L
    private const val READ_TIMEOUT = 300L
    private const val WRITE_TIMEOUT = 300L

    /**
     * Creates an OkHttpClient with logging and timeout settings.
     */
    fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Creates a Retrofit instance with the given base URL and client.
     */
    fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}