package com.example.ChatBlaze.data.model

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

class ApiKey(private val context: Context) {
    private val masterKeyAlias: MasterKey

    init {
        try {
            masterKeyAlias = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        } catch (e: GeneralSecurityException) {
            throw Exception("Failed to create MasterKey", e)
        } catch (e: IOException) {
            throw Exception("Failed to create MasterKey", e)
        }
    }

    private val sharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            "api_keys",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: GeneralSecurityException) {
        throw Exception("Failed to create EncryptedSharedPreferences", e)
    } catch (e: IOException) {
        throw Exception("Failed to create EncryptedSharedPreferences", e)
    }

    fun saveHuggingFaceApiKey(apiKey: String) {
        try {
            sharedPreferences.edit()
                .putString("HF_API_KEY", apiKey)
                .apply()
        } catch (e: Exception) {
            throw Exception("Failed to save Hugging Face API key", e)
        }
    }

    fun getHuggingFaceApiKey(): String? {
        return try {
            sharedPreferences.getString("HF_API_KEY", null)
        } catch (e: Exception) {
            throw Exception("Failed to get Hugging Face API key", e)
        }
    }

    fun saveOpenRouterApiKey(apiKey: String) {
        try {
            sharedPreferences.edit()
                .putString("OR_API_KEY", apiKey)
                .apply()
        } catch (e: Exception) {
            throw Exception("Failed to save OpenRouter API key", e)
        }
    }

    fun getOpenRouterApiKey(): String? {
        return try {
            sharedPreferences.getString("OR_API_KEY", null)
        } catch (e: Exception) {
            throw Exception("Failed to get OpenRouter API key", e)
        }
    }
}