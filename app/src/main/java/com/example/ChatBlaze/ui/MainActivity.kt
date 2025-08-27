package com.example.ChatBlaze.ui

import BotChatTheme
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ChatBlaze.data.database.modelDatabase.modelDao
import com.example.ChatBlaze.data.model.UserSettingsDataStore
import com.example.ChatBlaze.data.database.modelDatabase.modelDatabase
import com.example.ChatBlaze.ui.components.ModelScreen
import com.example.ChatBlaze.ui.components.chat.ChatScreen
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModel
import com.example.ChatBlaze.ui.viewmodel.setting.SettingViewModelFactory
import com.example.ChatBlaze.data.downlaod.ModelDownloaderScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // --- ADD THIS PERMISSION LAUNCHER ---
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        askNotificationPermission()

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

    // --- ADD THIS FUNCTION ---
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
                onNavigateToModels = { navController.navigate("models") },
                onNavigateToDownloader = { navController.navigate("model_downloader") }
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
        composable("model_downloader") {
            ModelDownloaderScreen(
                onNavigateBack = {
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
