package com.example.botchat.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.GeneralSecurityException
import java.io.IOException

class ApiKey(private val context: Context) {
    private val masterKeyAlias: MasterKey

    init {
        // Initialize the MasterKey
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
            throw Exception("Failed to save API key", e)
        }

    }

    fun getHuggingFaceApiKey(): String? {
        try {
            return sharedPreferences.getString("HF_API_KEY", null)
        } catch (e: Exception) {
            throw Exception("Failed to get API key", e)
        }
    }
}