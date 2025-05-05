package com.example.botchat.Repository

import com.example.botchat.database.ChatDao
import com.example.botchat.database.ChatMessage
import kotlinx.coroutines.flow.Flow
import android.util.Log

class ChatRepository(private val chatDao: ChatDao) {
    fun getAllChatMessages(): Flow<List<ChatMessage>> = chatDao.getAllChatMessages()

    suspend fun insertChatMessage(message: ChatMessage) {
        try {
            chatDao.insertChatMessage(message)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error inserting message: ${e.message}", e)
        }
    }

    suspend fun deleteAllChatMessages() {
        try {
            chatDao.deleteAllChatMessages()
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error deleting messages: ${e.message}", e)
        }
    }

    suspend fun deleteMessage(messageId: Long) {
        try {
            chatDao.deleteMessage(messageId)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error deleting message: ${e.message}", e)
        }
    }
}