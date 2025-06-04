package com.example.botchat.Repository

import com.example.botchat.database.ChatDao
import com.example.botchat.database.ChatMessage
import com.example.botchat.database.ChatSession
import kotlinx.coroutines.flow.Flow
import android.util.Log

class ChatRepository(private val chatDao: ChatDao) {
    fun getAllChatMessages(): Flow<List<ChatMessage>> = chatDao.getAllChatMessages()

    fun getMessagesBySession(sessionId: Long): Flow<List<ChatMessage>> = chatDao.getMessagesBySession(sessionId)

    fun getAllChatSessions(): Flow<List<ChatSession>> = chatDao.getAllChatSessions()

    suspend fun insertChatMessage(message: ChatMessage) {
        try {
            chatDao.insertChatMessage(message)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error inserting message: ${e.message}", e)
        }
    }

    suspend fun insertChatSession(session: ChatSession): Long {
        return chatDao.insertChatSession(session)
    }

    suspend fun deleteMessagesBySession(sessionId: Long) {
        try {
            chatDao.deleteMessagesBySession(sessionId)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error deleting messages by session: ${e.message}", e)
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