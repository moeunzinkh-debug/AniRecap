package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppLanguage
import com.example.data.AppSettings
import com.example.ui.AniRecapStudioViewModel
import com.example.ui.StudioNavScreen
import com.example.ui.screens.HomePage
import com.example.ui.screens.Page2Preview
import com.example.ui.screens.SettingsPage
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AniRecapStudioApp()
            }
        }
    }
}

@Composable
fun AniRecapStudioApp(
    studioViewModel: AniRecapStudioViewModel = viewModel()
) {
    val currentScreen by studioViewModel.currentScreen.collectAsState()
    val appLanguage by AppSettings.language.collectAsState()
    val isKhmer = appLanguage == AppLanguage.KHMER

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("studio_bottom_nav")
            ) {
                // Page 1: Home (Upload & Analyze)
                NavigationBarItem(
                    selected = currentScreen is StudioNavScreen.HomeUpload,
                    onClick = { studioViewModel.navigateTo(StudioNavScreen.HomeUpload) },
                    icon = { Icon(Icons.Default.UploadFile, contentDescription = "Home Page") },
                    label = {
                        Text(
                            text = if (isKhmer) "ទំព័រដើម (Upload)" else "Home (Upload)",
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CrimsonRedBright,
                        selectedTextColor = CrimsonRedBright,
                        indicatorColor = CrimsonRedBright.copy(alpha = 0.18f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_home_upload")
                )

                // Page 2: Monitor & Results (Script & CapCut Guide)
                NavigationBarItem(
                    selected = currentScreen is StudioNavScreen.Page2Preview,
                    onClick = { studioViewModel.navigateTo(StudioNavScreen.Page2Preview) },
                    icon = { Icon(Icons.Default.SlowMotionVideo, contentDescription = "Page 2 Monitor") },
                    label = {
                        Text(
                            text = if (isKhmer) "Page 2 (Monitor & Guide)" else "Page 2 (Monitor)",
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CrimsonRedBright,
                        selectedTextColor = CrimsonRedBright,
                        indicatorColor = CrimsonRedBright.copy(alpha = 0.18f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_page2_preview")
                )

                // Page 3: Settings
                NavigationBarItem(
                    selected = currentScreen is StudioNavScreen.Settings,
                    onClick = { studioViewModel.navigateTo(StudioNavScreen.Settings) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = {
                        Text(
                            text = if (isKhmer) "ការកំណត់ (Settings)" else "Settings",
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CrimsonRedBright,
                        selectedTextColor = CrimsonRedBright,
                        indicatorColor = CrimsonRedBright.copy(alpha = 0.18f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            when (currentScreen) {
                is StudioNavScreen.HomeUpload -> {
                    HomePage(studioViewModel = studioViewModel)
                }
                is StudioNavScreen.Page2Preview -> {
                    Page2Preview(studioViewModel = studioViewModel)
                }
                is StudioNavScreen.Settings -> {
                    SettingsPage()
                }
            }
        }
    }
}
