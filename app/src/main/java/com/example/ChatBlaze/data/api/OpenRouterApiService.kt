package com.example.ChatBlaze.data.api

import com.example.ChatBlaze.data.model.OpenRouterRequest
import com.example.ChatBlaze.data.model.OpenRouterModel
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeUnit

interface OpenRouterApiService {
    @GET("models")
    @Headers("HTTP-Referer: https://botchat.example.com", "X-Title: BotChat")
    suspend fun getModels(
        @Header("Authorization") authHeader: String
    ): OpenRouterModelResponse

    @POST("chat/completions")
    @Headers("HTTP-Referer: https://botchat.example.com", "X-Title: BotChat")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: OpenRouterRequest
    ): Map<String, Any>

    companion object {
        private const val BASE_URL = "https://openrouter.ai/api/v1/"

        fun create(): OpenRouterApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val newRequest = originalRequest.newBuilder()
                        .header("HTTP-Referer", "https://botchat.example.com/android") // Example
                        .header("X-Title", "BotChat Android") // Example
                        .build()
                    chain.proceed(newRequest)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(OpenRouterApiService::class.java)
        }

        fun createStreamingClient(apiKey: String, chatRequest: OpenRouterRequest): Flow<String> = callbackFlow {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .pingInterval(20, TimeUnit.SECONDS)
                .build()

            // Ensure the request is set to stream
            val streamingChatRequest = chatRequest.copy(stream = true)
            val requestBodyJson = Gson().toJson(streamingChatRequest)

            val request = Request.Builder()
                .url("${BASE_URL}chat/completions")
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .header("HTTP-Referer", "https://botchat.example.com/android-streaming")
                .header("X-Title", "BotChat Android Streaming")
                .post(
                    RequestBody.create(
                        "application/json".toMediaType(),
                        requestBodyJson
                    )
                )
                .build()

            val eventSourceFactory = EventSources.createFactory(client)
            val eventSource: EventSource = eventSourceFactory.newEventSource(
                request = request,
                listener = object : EventSourceListener() {
                    override fun onOpen(eventSource: EventSource, response: Response) {
                    }

                    override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                        if (data == "[DONE]") {
                            close()
                            return
                        }
                        val content = parseStreamData(data)
                        if (content.isNotEmpty()) {

                            trySend(content)
                        }
                    }

                    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                        val errorBodyString = response?.body?.string()
                        val errorMessage = buildString {
                            append("Streaming failed")
                            t?.let { append(": ${it.message}") }
                            response?.let { resp ->
                                append(" (HTTP ${resp.code})")
                                if (!errorBodyString.isNullOrEmpty()) {
                                    append(" - Body: $errorBodyString")
                                }
                            }
                        }
                        close(IOException(errorMessage, t))
                    }

                    override fun onClosed(eventSource: EventSource) {

                    }
                }
            )


            awaitClose {
                eventSource.cancel()
            }
        }

        private fun parseStreamData(data: String): String {
            return try {
                val jsonData = if (data.startsWith("data: ")) data.substringAfter("data: ").trim() else data.trim()

                if (jsonData.isEmpty() || jsonData == "[DONE]") {
                    return ""
                }

                val jsonObject = JsonParser.parseString(jsonData).asJsonObject
                jsonObject.getAsJsonArray("choices")
                    ?.firstOrNull()?.asJsonObject // Use firstOrNull for safety
                    ?.getAsJsonObject("delta")
                    ?.getAsJsonPrimitive("content")?.asString ?: ""
            } catch (e: Exception) {

                "An error occured"
            }
        }
    }
}

data class OpenRouterModelResponse(
    val data: List<OpenRouterModel>
)