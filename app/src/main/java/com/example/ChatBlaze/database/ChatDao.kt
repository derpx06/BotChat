package com.example.ChatBlaze.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert
    suspend fun insertChatMessage(message: ChatMessage)

    @Insert
    suspend fun insertChatSession(session: ChatSession): Long

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesBySession(sessionId: Long): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    fun getAllChatSessions(): Flow<List<ChatSession>>

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesBySession(sessionId: Long)

    @Query("DELETE FROM chat_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllChatMessages()

    @Query("DELETE FROM chat_sessions")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Long)
}