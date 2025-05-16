package com.example.botchat.database.modelDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface modelDao {
    @Insert
    suspend fun insertSelectedModel(model: SelectedModel)

    @Query("SELECT * FROM selected_models")
    fun getAllModels(): Flow<List<SelectedModel>>

    @Delete
    suspend fun deleteSelectedModel(model: SelectedModel)

    @Query("DELETE FROM selected_models")
    suspend fun deleteAllModels()
}