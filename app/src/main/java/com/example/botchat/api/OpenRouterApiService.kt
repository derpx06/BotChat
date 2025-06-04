package com.example.botchat.api

import com.example.botchat.data.OpenRouterRequest
import com.example.botchat.data.OpenRouterModel
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose // REQUIRED for callbackFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow // USE THIS for adapting callbacks
// import kotlinx.coroutines.flow.flow // We are replacing this for the streaming client
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody // For better error details
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
    @GET("models") // Retrofit typically handles leading slash based on baseUrl
    @Headers("HTTP-Referer: https://botchat.example.com", "X-Title: BotChat")
    suspend fun getModels(
        @Header("Authorization") authHeader: String
    ): OpenRouterModelResponse

    @POST("chat/completions")
    @Headers("HTTP-Referer: https://botchat.example.com", "X-Title: BotChat") // Good to have here too
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String, // e.g., "Bearer YOUR_API_KEY"
        @Body request: OpenRouterRequest
    ): Map<String, Any> // Consider a specific response data class

    companion object {
        // Double-check the correct base URL for OpenRouter API v1
        private const val BASE_URL = "https://openrouter.ai/api/v1/"

        fun create(): OpenRouterApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // General purpose timeout
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                // Optional: Add common headers via an interceptor if they apply to all calls
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
                .connectTimeout(60, TimeUnit.SECONDS)   // Timeout to establish connection
                .readTimeout(0, TimeUnit.SECONDS)       // NO read timeout for SSE, server keeps stream open
                .writeTimeout(60, TimeUnit.SECONDS)  // Timeout to send the request
                .pingInterval(20, TimeUnit.SECONDS) // Helps keep connection alive through proxies
                .build()

            // Ensure the request is set to stream
            val streamingChatRequest = chatRequest.copy(stream = true) // Assumes OpenRouterRequest is a data class
            val requestBodyJson = Gson().toJson(streamingChatRequest)

            val request = Request.Builder()
                .url("${BASE_URL}chat/completions") // Ensure full URL for direct OkHttp
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream") // Standard for SSE
                // Add OpenRouter specific recommended headers
                .header("HTTP-Referer", "https://botchat.example.com/android-streaming")
                .header("X-Title", "BotChat Android Streaming")
                .post(
                    okhttp3.RequestBody.create(
                        "application/json".toMediaType(),
                        requestBodyJson
                    )
                )
                .build()

            val eventSourceFactory = EventSources.createFactory(client)
            val eventSource: EventSource = eventSourceFactory.newEventSource( // Removed nullable type
                request = request,
                listener = object : EventSourceListener() {
                    override fun onOpen(eventSource: EventSource, response: Response) {
                        // Connection opened - can log if needed
                    }

                    override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                        if (data == "[DONE]") {
                            close() // IMPORTANT: Close the flow when streaming is done
                            return
                        }
                        val content = parseStreamData(data)
                        if (content.isNotEmpty()) {
                            // Use trySend for callbackFlow, it's non-blocking.
                            // Check channelResult.isSuccess if needed, or handle potential ClosedSendChannelException.
                            trySend(content)
                        }
                    }

                    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                        val errorBodyString = response?.body?.string() // Consume body only once
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
                        // IMPORTANT: Close the flow with an exception
                        close(IOException(errorMessage, t))
                    }

                    override fun onClosed(eventSource: EventSource) {
                        // Connection closed by server or client - can log if needed
                        // Flow might already be closed via [DONE] or onFailure
                    }
                }
            )

            // This block is executed when the Flow is cancelled by the collector
            // or when close() is called. Essential for cleaning up resources.
            awaitClose {
                eventSource.cancel()
            }
        }

        private fun parseStreamData(data: String): String {
            return try {
                // Handle "data: " prefix if present. Trim whitespace.
                val jsonData = if (data.startsWith("data: ")) data.substringAfter("data: ").trim() else data.trim()

                // If after stripping "data: " and trimming, it's empty or [DONE], return empty.
                if (jsonData.isEmpty() || jsonData == "[DONE]") {
                    return ""
                }

                val jsonObject = com.google.gson.JsonParser.parseString(jsonData).asJsonObject
                jsonObject.getAsJsonArray("choices")
                    ?.firstOrNull()?.asJsonObject // Use firstOrNull for safety
                    ?.getAsJsonObject("delta")
                    ?.getAsJsonPrimitive("content")?.asString ?: ""
            } catch (e: Exception) {
                // Consider logging the exception and the data that caused it
                // android.util.Log.e("ParseStreamData", "Error parsing SSE: '$data'", e)
                "An error occured" // Return empty on error to prevent crashing the flow
            }
        }
    }
}

data class OpenRouterModelResponse(
    val data: List<OpenRouterModel>
)