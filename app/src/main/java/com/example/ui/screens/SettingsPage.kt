package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApiKeyEntry
import com.example.data.AppLanguage
import com.example.data.AppSettings
import com.example.data.AppThemeMode
import com.example.ui.components.StudioKicker
import com.example.ui.components.StudioStyle
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val themeMode by AppSettings.themeMode.collectAsState()
    val appLanguage by AppSettings.language.collectAsState()
    val apiKeys by AppSettings.apiKeys.collectAsState()
    val activeKeyId by AppSettings.activeKeyId.collectAsState()
    val useCustomProvider by AppSettings.useCustomProvider.collectAsState()
    val openRouterBaseUrl by AppSettings.openRouterBaseUrl.collectAsState()
    val openRouterApiKey by AppSettings.openRouterApiKey.collectAsState()
    val openRouterModel by AppSettings.openRouterModel.collectAsState()

    var showAddKeyDialog by remember { mutableStateOf(false) }
    var newKeyLabel by remember { mutableStateOf("") }
    var newKeyValue by remember { mutableStateOf("") }
    var newKeyProvider by remember { mutableStateOf("Google Gemini") }

    var customBaseUrlInput by remember(openRouterBaseUrl) { mutableStateOf(openRouterBaseUrl) }
    var customApiKeyInput by remember(openRouterApiKey) { mutableStateOf(openRouterApiKey) }
    var customModelInput by remember(openRouterModel) { mutableStateOf(openRouterModel) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(StudioStyle.screenPadding)
            .padding(top = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(StudioStyle.sectionGap)
    ) {
        // Same header rhythm as the other studio pages.
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            StudioKicker("Page 3 · Preferences")
            Text(
                text = "ការកំណត់ (Settings)",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "API keys, OpenRouter, theme, language & cache",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ==========================================
        // 1. Add API key save, can add many keys more
        // ==========================================
        SettingsSectionCard(title = "API Keys Management (គ្រប់គ្រង API Keys)") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Keys (${apiKeys.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { showAddKeyDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRedBright),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_key_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Key", fontSize = 11.sp)
                    }
                }

                if (apiKeys.isEmpty()) {
                    Text(
                        text = "មិនទាន់មាន API Key នៅឡើយទេ។ ចុច Add Key ដើម្បីបញ្ចូល Gemini API Key ថ្មី។",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    apiKeys.forEach { entry ->
                        val isActive = entry.id == activeKeyId
                        Surface(
                            color = if (isActive) CrimsonRedBright.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, CrimsonRedBright) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { AppSettings.setActiveKeyId(entry.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = isActive,
                                        onClick = { AppSettings.setActiveKeyId(entry.id) },
                                        colors = RadioButtonDefaults.colors(selectedColor = CrimsonRedBright)
                                    )
                                    Column {
                                        Text(
                                            text = entry.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        val masked = if (entry.key.length > 8) {
                                            entry.key.take(4) + "••••••••" + entry.key.takeLast(4)
                                        } else "••••••••"
                                        Text(
                                            text = "$masked (${entry.provider})",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        AppSettings.removeApiKey(entry.id)
                                        Toast.makeText(context, "បានលុប API Key រួចរាល់", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Key",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. Custom key from other platform (Example: Openrouter)
        // ==========================================
        SettingsSectionCard(title = "Custom Platform / OpenRouter Integration") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Use Custom Endpoint (OpenRouter)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "បើកប្រើប្រាស់ Custom API Key ពីវេទិកាផ្សេងទៀតដូចជា OpenRouter",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = useCustomProvider,
                        onCheckedChange = { enabled ->
                            AppSettings.setCustomProvider(
                                enabled,
                                customBaseUrlInput,
                                customApiKeyInput,
                                customModelInput
                            )
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRedBright, checkedTrackColor = CrimsonRedBright.copy(alpha = 0.5f))
                    )
                }

                AnimatedVisibility(visible = useCustomProvider) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customBaseUrlInput,
                            onValueChange = { customBaseUrlInput = it },
                            label = { Text("Base URL (e.g. OpenRouter)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customApiKeyInput,
                            onValueChange = { customApiKeyInput = it },
                            label = { Text("OpenRouter API Key (sk-or-...)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customModelInput,
                            onValueChange = { customModelInput = it },
                            label = { Text("Custom Model Name") },
                            placeholder = { Text("google/gemini-2.5-flash") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                AppSettings.setCustomProvider(
                                    true,
                                    customBaseUrlInput,
                                    customApiKeyInput,
                                    customModelInput
                                )
                                Toast.makeText(context, "បានរក្សាទុក OpenRouter Configuration", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ManaViolet),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Save OpenRouter Settings")
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. Theme: system. Auto, Dark and white
        // ==========================================
        SettingsSectionCard(title = "Theme (រូបរាងកម្មវិធី)") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ThemeOptionRow(
                    label = "System Auto (តាមប្រព័ន្ធ)",
                    isSelected = themeMode == AppThemeMode.SYSTEM,
                    onClick = { AppSettings.setThemeMode(AppThemeMode.SYSTEM) }
                )
                ThemeOptionRow(
                    label = "Dark Mode (ងងឹត)",
                    isSelected = themeMode == AppThemeMode.DARK,
                    onClick = { AppSettings.setThemeMode(AppThemeMode.DARK) }
                )
                ThemeOptionRow(
                    label = "White / Light Mode (ស)",
                    isSelected = themeMode == AppThemeMode.LIGHT,
                    onClick = { AppSettings.setThemeMode(AppThemeMode.LIGHT) }
                )
            }
        }

        // ==========================================
        // 4. Language: Khmer / English
        // ==========================================
        SettingsSectionCard(title = "Language (ភាសា)") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = appLanguage == AppLanguage.KHMER,
                    onClick = { AppSettings.setLanguage(AppLanguage.KHMER) },
                    label = { Text("Khmer (ភាសាខ្មែរ)", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CrimsonRedBright,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = appLanguage == AppLanguage.ENGLISH,
                    onClick = { AppSettings.setLanguage(AppLanguage.ENGLISH) },
                    label = { Text("English", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CrimsonRedBright,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ==========================================
        // 5. Clear Cache
        // ==========================================
        SettingsSectionCard(title = "Storage & Cache (ទំហំផ្ទុក)") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "សម្អាតឯកសារបណ្តោះអាសន្ន (Clear Cache)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ជួយឲ្យកម្មវិធីដំណើរការលឿន និងសន្សំសំចៃទំហំទូរស័ព្ទ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = {
                        AppSettings.clearCache(context)
                        Toast.makeText(context, "បានសម្អាត Cache ជោគជ័យ! (Cache Cleared)", Toast.LENGTH_SHORT).show()
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                ) {
                    Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Cache", color = ErrorRed, fontSize = 11.sp)
                }
            }
        }

        // ==========================================
        // 6. About
        // ==========================================
        SettingsSectionCard(title = "About (អំពីកម្មវិធី)") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "AniRecap Studio (Movie & Anime Recap)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CrimsonRedBright
                )
                Text(
                    text = "Version 1.0.0 (Production Release)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "កម្មវិធីបង្កើត Script សម្រាយសាច់រឿងភាសាខ្មែរតាមក្បួនខ្នាត Master Prompt (Plot-First 80% Story + 20% Recap + Rank/Faction Tracking) និងបង្កើត CapCut Editing Roadmaps ដោយស្វ័យប្រវត្តិតាមរយៈ Gemini 3.5, 3.6, 3.7, 3.8 និង OpenRouter។",
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Modal Dialog: Add API Key
    if (showAddKeyDialog) {
        AlertDialog(
            onDismissRequest = { showAddKeyDialog = false },
            title = {
                Text("Add New API Key", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newKeyLabel,
                        onValueChange = { newKeyLabel = it },
                        label = { Text("Key Label / Name") },
                        placeholder = { Text("e.g. My Gemini Pro Key") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newKeyValue,
                        onValueChange = { newKeyValue = it },
                        label = { Text("API Key Value") },
                        placeholder = { Text("AIzaSy...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = newKeyProvider == "Google Gemini",
                            onClick = { newKeyProvider = "Google Gemini" },
                            label = { Text("Gemini") }
                        )
                        FilterChip(
                            selected = newKeyProvider == "OpenRouter",
                            onClick = { newKeyProvider = "OpenRouter" },
                            label = { Text("OpenRouter") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newKeyValue.isNotBlank()) {
                            AppSettings.addApiKey(newKeyLabel, newKeyValue, newKeyProvider)
                            showAddKeyDialog = false
                            newKeyLabel = ""
                            newKeyValue = ""
                            Toast.makeText(context, "បានរក្សាទុក API Key ថ្មី", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRedBright)
                ) {
                    Text("Save Key")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddKeyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = CrimsonRedBright
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun ThemeOptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) CrimsonRedBright.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CrimsonRedBright) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = CrimsonRedBright)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
