package com.example.botchat.Data

// Server Request/Response
data class ChatRequest(
    val messages: List<Message>,
    val model: String? = null
)

data class Message(
    val role: String = "user",
    val content: String
)

data class ChatResponse(
    val response: ResponseData
)

data class ResponseData(
    val text: String,
    val formatted: String
)

// UI Model
data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val id: String
)