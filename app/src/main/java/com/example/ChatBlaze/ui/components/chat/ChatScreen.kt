package com.example.ChatBlaze.ui.components.chat

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    onNavigateToDownloader: () -> Unit,
    onNavigateToSettings:()-> Unit
) {
    val context = LocalContext.current
    val modelDao = remember { modelDatabase.getDatabase(context).modelDao() }
    val application = (LocalContext.current.applicationContext as Application)

    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            dataStore = UserSettingsDataStore(context),
            chatRepository = ChatRepository(ChatDatabase.getDatabase(context).chatDao()),
            application = application
        )
    )
    val settingViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(UserSettingsDataStore(context))
    )
    val uiState by chatViewModel.uiState.collectAsState()

    ChatDrawer(
        chatViewModel = chatViewModel,
        settingViewModel = settingViewModel,
        onNavigateToModels = onNavigateToModels,
        onNavigateToDownloader = onNavigateToDownloader,
        modelDao = modelDao,
        isModelLoading = uiState.isModelLoading,
        onNavigateToSettings = onNavigateToSettings
    )
}
