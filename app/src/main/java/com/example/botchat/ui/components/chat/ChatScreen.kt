package com.example.botchat.ui.components.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botchat.Repository.ChatRepository
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.database.ChatDatabase
import com.example.botchat.database.modelDatabase.modelDatabase
import com.example.botchat.viewmodel.Chat.ChatViewModel
import com.example.botchat.viewmodel.Chat.ChatViewModelFactory
import com.example.botchat.viewmodel.setting.SettingViewModel
import com.example.botchat.viewmodel.setting.SettingViewModelFactory

@Composable
fun ChatScreen(
    onNavigateToModels: () -> Unit = {}
) {
    val context = LocalContext.current
    val modelDao = remember { modelDatabase.getDatabase(context).modelDao() }
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            UserSettingsDataStore(context),
            ChatRepository(ChatDatabase.getDatabase(context).chatDao())
        )
    )
    val settingViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(UserSettingsDataStore(context))
    )

    ChatDrawer(
        chatViewModel = chatViewModel,
        settingViewModel = settingViewModel,
        onNavigateToModels = onNavigateToModels,
        modelDao = modelDao
    )
}