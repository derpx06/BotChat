package com.example.botchat.database.modelDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity to represent a selected model in the database
@Entity(tableName = "selected_models")
data class SelectedModel(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?
)