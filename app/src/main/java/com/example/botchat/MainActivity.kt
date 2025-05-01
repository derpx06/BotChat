package com.example.botchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.ui.components.chat.ChatScreen
import com.example.botchat.viewmodel.SettingViewModel
import com.example.botchat.viewmodel.SettingViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create your DataStore and factory
        val dataStore = UserSettingsDataStore(applicationContext)
        val factory = SettingViewModelFactory(dataStore)
        val settingViewModel = ViewModelProvider(this, factory)
            .get(SettingViewModel::class.java)

        setContent {
            ChatScreen(settingViewModel = settingViewModel)
        }
    }
}
