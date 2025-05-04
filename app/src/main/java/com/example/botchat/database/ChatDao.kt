package com.example.botchat.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert
    suspend fun insertChatMessage(chatMessage: ChatMessageEntity)
    @Query("SELECT * FROM ChatMessageEntity ORDER BY TimeStamp ASC")
    suspend fun getAllChatMessages(): Flow<List<ChatMessageEntity>>
    @Query("DELETE FROM ChatMessageEntity")
    suspend fun deleteAllChatMessages()
    @Query("DELETE FROM chatmessageentity WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Long)
}