package com.example.ChatBlaze.data.repository

import com.example.ChatBlaze.data.database.ChatDao
import com.example.ChatBlaze.data.database.ChatMessage
import com.example.ChatBlaze.data.database.ChatSession
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {
    fun getMessagesBySession(sessionId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessagesBySession(sessionId)
    }

    fun getAllChatSessions(): Flow<List<ChatSession>> {
        return chatDao.getAllChatSessions()
    }

    suspend fun insertChatMessage(message: ChatMessage) {
        chatDao.insertChatMessage(message)
    }

    suspend fun insertChatSession(session: ChatSession): Long {
        return chatDao.insertChatSession(session)
    }

    suspend fun deleteMessagesBySession(sessionId: Long) {
        chatDao.deleteMessagesBySession(sessionId)
    }

    suspend fun deleteSession(sessionId: Long) {
        chatDao.deleteSession(sessionId)
        chatDao.deleteMessagesBySession(sessionId)
    }

    suspend fun deleteAllSessions() {
        chatDao.deleteAllSessions()
        chatDao.deleteAllChatMessages()
    }

    suspend fun deleteMessage(messageId: Long) {
        chatDao.deleteMessage(messageId)
    }
}