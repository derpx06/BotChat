package com.example.botchat.api//package com.example.chatapp.api
//
//import com.example.botchat.api.ApiService
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//
//import java.util.concurrent.TimeUnit
//
//object RetrofitClient {
//    private const val BASE_URL = "https://your-tunnel.trycloudflare.com/" // Replace with your actual Cloudflare Tunnel URL
//
//    private val okHttpClient = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(60, TimeUnit.SECONDS)
//        .writeTimeout(30, TimeUnit.SECONDS)
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        })
//        .build()
//
//    val apiService: ApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }
//}