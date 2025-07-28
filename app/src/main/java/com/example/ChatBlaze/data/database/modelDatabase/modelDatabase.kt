package com.example.ChatBlaze.data.database.modelDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SelectedModel::class], version = 2, exportSchema = false)
abstract class modelDatabase : RoomDatabase() {
    abstract fun modelDao(): modelDao

    companion object {
        @Volatile
        private var INSTANCE: modelDatabase? = null

        fun getDatabase(context: Context): modelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    modelDatabase::class.java,
                    "model_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}