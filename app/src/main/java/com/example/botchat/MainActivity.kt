package com.example.botchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.ui.components.chat.ChatScreen
import com.example.botchat.ui.theme.BotChatTheme
import com.example.botchat.viewmodel.setting.SettingViewModel
import com.example.botchat.viewmodel.setting.SettingViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingViewModel: SettingViewModel = viewModel(
                factory = SettingViewModelFactory(UserSettingsDataStore(this))
            )
            val isDarkTheme = settingViewModel.getDarkModeEnabled()
            BotChatTheme(darkTheme = isDarkTheme) {
                ChatScreen(
                    settingViewModel = settingViewModel)
            }
        }
    }
}
