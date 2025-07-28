package com.example.ChatBlaze.data

// Server Request/Response
data class ChatRequest(
    val messages: List<Message>,
    val model: String? = null
)

data class Message(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatResponse(
    val response: ResponseData
)

data class ResponseData(
    val text: String,
    val formatted: String
)

