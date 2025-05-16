package com.example.botchat.database.modelDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_models")
data class SelectedModel(
    @PrimaryKey val modelId: String,
    val name: String,
    val description: String
)