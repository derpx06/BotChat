package com.example.botchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.ui.components.chat.ChatScreen
import com.example.botchat.ui.theme.BotChatTheme
import com.example.botchat.viewmodel.SettingViewModel
import com.example.botchat.viewmodel.SettingViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingViewModel: SettingViewModel = viewModel(
                factory = SettingViewModelFactory(UserSettingsDataStore(this))
            )
            val isDarkTheme by settingViewModel.darkModeEnabled.collectAsState(initial = false)
            BotChatTheme(darkTheme = isDarkTheme) {
                ChatScreen()
            }
        }
    }
}