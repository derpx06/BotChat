package com.example.botchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.ui.components.chat.ChatScreen
import com.example.botchat.ui.theme.BotChatTheme
import com.example.botchat.viewmodel.SettingViewModel
import com.example.botchat.viewmodel.SettingViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
