//package com.example.chatapp.repository
//
//import com.example.botchat.Data.ChatResponse
//import com.example.botchat.Data.ChatRequest
//
//import com.google.gson.Gson
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.onStart
//import java.io.BufferedReader
//import java.io.InputStreamReader
//
//class ChatRepository {
//    fun sendMessage(message: String): Flow<ChatResponse> = flow {
//        val request = ChatRequest(listOf(ChatRequest.Message(content = message)), "smollm2:360m")
//        val response = RetrofitClient.apiService.sendMessage(request)
//
//        if (response.isSuccessful) {
//            response.body()?.let { body ->
//                val reader = BufferedReader(InputStreamReader(body.byteStream()))
//                reader.useLines { lines ->
//                    lines.forEach { line ->
//                        if (line.startsWith("data: ")) {
//                            val json = line.substring(6).trim()
//                            if (json.isNotEmpty()) {
//                                try {
//                                    val chatResponse = Gson().fromJson(json, ChatResponse::class.java)
//                                    emit(chatResponse)
//                                } catch (e: Exception) {
//                                    println("Parse error: ${e.message}")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            val errorBody = response.errorBody()?.string() ?: "Unknown error"
//            throw Exception("API error: ${response.code()} - $errorBody")
//        }
//    }.onStart {
//        println("Sending message: $message")
//    }.catch { e ->
//        println("Error sending message: ${e.message}")
//        throw e
//    }
//}