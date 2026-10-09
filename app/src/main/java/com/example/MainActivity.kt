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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
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
                tonalElevation = 2.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("studio_bottom_nav")
            ) {
                // Three equal destinations: icon + one short, single-line label.
                StudioNavDestination(
                    label = if (isKhmer) "បញ្ចូល" else "Upload",
                    icon = Icons.Default.UploadFile,
                    selected = currentScreen is StudioNavScreen.HomeUpload,
                    testTag = "nav_home_upload",
                    onClick = { studioViewModel.navigateTo(StudioNavScreen.HomeUpload) }
                )
                StudioNavDestination(
                    label = if (isKhmer) "ត្រួតពិនិត្យ" else "Monitor",
                    icon = Icons.Default.SlowMotionVideo,
                    selected = currentScreen is StudioNavScreen.Page2Preview,
                    testTag = "nav_page2_preview",
                    onClick = { studioViewModel.navigateTo(StudioNavScreen.Page2Preview) }
                )
                StudioNavDestination(
                    label = if (isKhmer) "ការកំណត់" else "Settings",
                    icon = Icons.Default.Settings,
                    selected = currentScreen is StudioNavScreen.Settings,
                    testTag = "nav_settings",
                    onClick = { studioViewModel.navigateTo(StudioNavScreen.Settings) }
                )
            }
        }
    ) { innerPadding ->
        // Scaffold already resolves the system-bar insets into innerPadding, so the
        // pages are not padded twice at the top.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

/**
 * One bottom-navigation destination. Labels are short and always render on a
 * single line so the three items stay visually equal, and only the selected
 * item carries the accent colour.
 */
@Composable
private fun RowScope.StudioNavDestination(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        },
        alwaysShowLabel = true,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRedBright,
            selectedTextColor = CrimsonRedBright,
            indicatorColor = CrimsonRedBright.copy(alpha = 0.14f),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.testTag(testTag)
    )
}
