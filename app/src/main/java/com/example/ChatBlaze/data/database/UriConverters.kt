package com.example.ChatBlaze.data.database

import androidx.room.TypeConverter

class UriConverters {

    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        return value.split(',')
    }


    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }
}
