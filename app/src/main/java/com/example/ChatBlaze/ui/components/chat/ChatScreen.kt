package com.example.ChatBlaze.ui.components.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ChatBlaze.data.repository.ChatRepository
import com.example.ChatBlaze.data.model.UserSettingsDataStore
import com.example.ChatBlaze.data.database.ChatDatabase
import com.example.ChatBlaze.data.database.modelDatabase.modelDatabase
import com.example.ChatBlaze.ui.viewmodel.Chat.ChatViewModel
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModel
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModelFactory
import com.example.ChatBlaze.ui.viewmodel.Chat.ChatViewModelFactory

@Composable
fun ChatScreen(
    onNavigateToModels: () -> Unit,
    onNavigateToDownloader: () -> Unit
) {
    val context = LocalContext.current
    val modelDao = remember { modelDatabase.getDatabase(context).modelDao() }
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            dataStore = UserSettingsDataStore(context),
            chatRepository = ChatRepository(ChatDatabase.getDatabase(context).chatDao()),
            context = context
        )
    )
    val settingViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(UserSettingsDataStore(context))
    )

    ChatDrawer(
        chatViewModel = chatViewModel,
        settingViewModel = settingViewModel,
        onNavigateToModels = onNavigateToModels,
        onNavigateToDownloader = onNavigateToDownloader,
        modelDao = modelDao
    )
}
