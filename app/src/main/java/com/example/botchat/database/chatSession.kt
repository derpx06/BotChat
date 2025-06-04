package com.example.botchat.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val title: String = "New Chat",
    val timestamp: Long = System.currentTimeMillis()
)