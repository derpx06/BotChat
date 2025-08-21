package com.example.ChatBlaze.ui

import BotChatTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ChatBlaze.data.database.modelDatabase.modelDao
import com.example.ChatBlaze.data.model.UserSettingsDataStore
import com.example.ChatBlaze.data.database.modelDatabase.modelDatabase
import com.example.ChatBlaze.ui.components.ModelScreen
import com.example.ChatBlaze.ui.components.chat.ChatScreen
import com.example.ChatBlaze.ui.theme.*
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModel
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingViewModel: SettingViewModel = viewModel(
                factory = SettingViewModelFactory(UserSettingsDataStore(this))
            )
            val isDarkTheme = settingViewModel.getDarkModeEnabled()
            val modelDao = modelDatabase.getDatabase(this).modelDao()
            BotChatTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    settingViewModel = settingViewModel,
                    modelDao = modelDao
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    settingViewModel: SettingViewModel,
    modelDao: modelDao
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") {
            ChatScreen(
                //settingViewModel = settingViewModel,
                //modelDao = modelDao,
                onNavigateToModels = { navController.navigate("models") }
            )
        }
        composable("models") {
            ModelScreen(
                settingViewModel = settingViewModel,
                modelDao = modelDao,
                onModelSelected = { modelId ->
                    settingViewModel.updateOpenRouterModel(modelId)
                    navController.popBackStack()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BotChatTheme {
        val context = LocalContext.current
        AppNavigation(
            settingViewModel = viewModel(),
            modelDao = modelDatabase.getDatabase(context).modelDao()
        )
    }
}