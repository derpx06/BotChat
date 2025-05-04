package com.example.botchat.Repository

import com.example.botchat.database.ChatDao
import com.example.botchat.database.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository (private val dao:ChatDao){
    suspend fun insertChatMessage(chatMessage: ChatMessageEntity) {
        dao.insertChatMessage(chatMessage)
    }
    suspend fun getALlChatMessages() : Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    suspend fun deleteAllChatMessages() = dao.deleteAllChatMessages()
    suspend fun deleteMessage(messageId: Long) = dao.deleteMessage(messageId)
}
