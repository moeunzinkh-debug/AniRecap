package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.AppLanguage
import com.example.data.AppSettings
import com.example.data.AppThemeMode
import com.example.data.CacheBreakdown
import com.example.data.CacheCleanupResult
import com.example.data.CacheStats
import com.example.data.formatBytes
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioChoiceRow
import com.example.ui.components.StudioDivider
import com.example.ui.components.StudioKeyValueRow
import com.example.ui.components.StudioKicker
import com.example.ui.components.StudioPrimaryButton
import com.example.ui.components.StudioSectionHeader
import com.example.ui.components.StudioSegment
import com.example.ui.components.StudioSegmentedControl
import com.example.ui.components.StudioStatusPill
import com.example.ui.components.StudioStyle
import com.example.ui.components.StudioTag
import com.example.ui.theme.CrimsonRedBright
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    // Cache state — measured from disk, never guessed.
    var cache by remember { mutableStateOf<CacheBreakdown?>(null) }
    var lastCleanup by remember { mutableStateOf<CacheCleanupResult?>(null) }
    var isMeasuringCache by remember { mutableStateOf(true) }
    var isClearingCache by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var cacheMeasureToken by remember { mutableIntStateOf(0) }

    LaunchedEffect(cacheMeasureToken, isClearingCache) {
        if (isClearingCache) return@LaunchedEffect
        isMeasuringCache = true
        cache = CacheStats.measure(context)
        isMeasuringCache = false
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(StudioStyle.screenPadding)
            .padding(top = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(StudioStyle.sectionGap)
    ) {
        // ==========================================================
        // Header — same rhythm as the other studio pages
        // ==========================================================
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            StudioKicker("Page 3 · Preferences")
            Text(
                text = "ការកំណត់ (Settings)",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 30.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "API keys, custom endpoint, theme, language and cache",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ==========================================================
        // 1. API keys
        // ==========================================================
        SettingsCard(
            kicker = "Providers",
            title = "API Keys",
            subtitle = if (apiKeys.isEmpty()) {
                "No key saved yet"
            } else {
                "${apiKeys.size} saved · active: ${apiKeys.firstOrNull { it.id == activeKeyId }?.label ?: "none"}"
            },
            trailing = {
                StudioPrimaryButton(
                    text = "Add key",
                    icon = Icons.Default.Add,
                    height = 38.dp,
                    onClick = { showAddKeyDialog = true },
                    modifier = Modifier.testTag("add_key_button")
                )
            }
        ) {
            if (apiKeys.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "មិនទាន់មាន API Key នៅឡើយទេ។ ចុច Add Key ដើម្បីបញ្ចូល Gemini API Key ថ្មី។",
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                apiKeys.forEach { entry ->
                    val isActive = entry.id == activeKeyId
                    StudioCard(
                        containerColor = if (isActive) {
                            CrimsonRedBright.copy(alpha = 0.08f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        borderColor = if (isActive) {
                            CrimsonRedBright.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                        cornerRadius = StudioStyle.innerRadius,
                        contentPadding = 12.dp,
                        verticalSpacing = 0.dp,
                        onClick = { AppSettings.setActiveKeyId(entry.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = entry.maskedKey(),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isActive) {
                                StudioStatusPill(text = "Active", color = SuccessGreen)
                            } else {
                                StudioTag(label = "Provider", value = entry.provider)
                            }

                            IconButton(
                                onClick = {
                                    AppSettings.removeApiKey(entry.id)
                                    Toast.makeText(context, "បានលុប API Key រួចរាល់", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete ${entry.label}",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================================
        // 2. Custom endpoint (OpenRouter)
        // ==========================================================
        SettingsCard(
            kicker = "Advanced",
            title = "Custom endpoint",
            subtitle = "Use another provider such as OpenRouter instead of Gemini"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Use Custom Endpoint (OpenRouter)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "បើកប្រើប្រាស់ Custom API Key ពីវេទិកាផ្សេងទៀតដូចជា OpenRouter",
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
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
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextPrimary,
                        checkedTrackColor = CrimsonRedBright,
                        checkedBorderColor = Color.Transparent
                    )
                )
            }

            AnimatedVisibility(visible = useCustomProvider) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = customBaseUrlInput,
                        onValueChange = { customBaseUrlInput = it },
                        label = { Text("Base URL") },
                        placeholder = { Text("https://openrouter.ai/api/v1") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(StudioStyle.innerRadius)
                    )
                    OutlinedTextField(
                        value = customApiKeyInput,
                        onValueChange = { customApiKeyInput = it },
                        label = { Text("API key") },
                        placeholder = { Text("sk-or-...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(StudioStyle.innerRadius)
                    )
                    OutlinedTextField(
                        value = customModelInput,
                        onValueChange = { customModelInput = it },
                        label = { Text("Model name") },
                        placeholder = { Text("google/gemini-2.5-flash") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(StudioStyle.innerRadius)
                    )
                    StudioPrimaryButton(
                        text = "Save endpoint settings",
                        icon = Icons.Default.Save,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            AppSettings.setCustomProvider(
                                true,
                                customBaseUrlInput,
                                customApiKeyInput,
                                customModelInput
                            )
                            Toast.makeText(
                                context,
                                "បានរក្សាទុក OpenRouter Configuration",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }

        // ==========================================================
        // 3. Theme
        // ==========================================================
        SettingsCard(
            kicker = "Appearance",
            title = "Theme",
            subtitle = "រូបរាងកម្មវិធី"
        ) {
            StudioChoiceRow(
                label = "System Auto (តាមប្រព័ន្ធ)",
                description = "Follow the device light/dark setting",
                icon = Icons.Default.BrightnessAuto,
                selected = themeMode == AppThemeMode.SYSTEM,
                onClick = { AppSettings.setThemeMode(AppThemeMode.SYSTEM) }
            )
            StudioChoiceRow(
                label = "Dark Mode (ងងឹត)",
                description = "Cinematic dark surface for long editing sessions",
                icon = Icons.Default.DarkMode,
                selected = themeMode == AppThemeMode.DARK,
                onClick = { AppSettings.setThemeMode(AppThemeMode.DARK) }
            )
            StudioChoiceRow(
                label = "Light Mode (ស)",
                description = "Bright theme for daylight review",
                icon = Icons.Default.LightMode,
                selected = themeMode == AppThemeMode.LIGHT,
                onClick = { AppSettings.setThemeMode(AppThemeMode.LIGHT) }
            )
        }

        // ==========================================================
        // 4. Language — segmented control instead of two chips
        // ==========================================================
        SettingsCard(
            kicker = "Language",
            title = "ភាសា (App language)",
            subtitle = "Affects labels across the studio"
        ) {
            StudioSegmentedControl(
                options = listOf(
                    StudioSegment("ខ្មែរ", Icons.Default.Translate),
                    StudioSegment("English", Icons.Default.Translate)
                ),
                selectedIndex = if (appLanguage == AppLanguage.KHMER) 0 else 1,
                onSelect = { index ->
                    AppSettings.setLanguage(
                        if (index == 0) AppLanguage.KHMER else AppLanguage.ENGLISH
                    )
                }
            )
        }

        // ==========================================================
        // 5. Storage & cache — real numbers, confirmation, honest result
        // ==========================================================
        SettingsCard(
            kicker = "Storage & cache",
            title = "Cache",
            subtitle = when {
                isMeasuringCache && cache == null -> "Measuring…"
                cache == null -> "Measured after the last run"
                cache?.isEmpty == true -> "Nothing to clean up"
                else -> "${cache?.fileCount ?: 0} files across the app cache folders"
            },
            trailing = {
                IconButton(
                    onClick = { cacheMeasureToken++ },
                    enabled = !isMeasuringCache && !isClearingCache,
                    modifier = Modifier.size(44.dp).testTag("cache_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Re-measure cache size",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        ) {
            val bytes = cache?.totalBytes ?: 0L

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isMeasuringCache && cache == null) "—" else formatBytes(bytes),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ជួយឲ្យកម្មវិធីដំណើរការលឿន និងសន្សំសំចៃទំហំទូរស័ព្ទ",
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (bytes > 0L) {
                    StudioStatusPill(
                        text = "Ready to clear",
                        color = WarningAmber,
                        icon = Icons.Default.WarningAmber
                    )
                } else {
                    StudioStatusPill(text = "Clean", color = SuccessGreen, icon = Icons.Default.Check)
                }
            }

            StudioDivider()

            StudioKeyValueRow(
                label = "Internal cache (upload temp files)",
                value = formatBytes(cache?.internalCacheBytes ?: 0L)
            )
            StudioKeyValueRow(
                label = "Code cache (dalvik)",
                value = formatBytes(cache?.codeCacheBytes ?: 0L)
            )
            StudioKeyValueRow(
                label = "External cache",
                value = formatBytes(cache?.externalCacheBytes ?: 0L)
            )

            StudioDivider()

            if (isClearingCache) {
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = StudioStyle.controlHeight),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = CrimsonRedBright
                    )
                    Text(
                        text = "Clearing cache…",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                StudioPrimaryButton(
                    text = "Clear cache",
                    icon = Icons.Default.DeleteSweep,
                    enabled = bytes > 0L,
                    modifier = Modifier.fillMaxWidth().testTag("clear_cache_button"),
                    onClick = { showClearCacheDialog = true }
                )
            }

            lastCleanup?.let { result ->
                Text(
                    text = if (result.filesDeleted > 0) {
                        val busy = if (result.entriesFailed > 0) {
                            " · ${result.entriesFailed} entries were busy"
                        } else {
                            ""
                        }
                        "បានសម្អាតរួចរាល់ · freed ${formatBytes(result.bytesFreed)} · ${result.filesDeleted} files$busy"
                    } else {
                        "Nothing was removed — the cache folders are already empty"
                    },
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = if (result.entriesFailed > 0) WarningAmber else SuccessGreen
                )
            }
        }

        // ==========================================================
        // 6. About — values come from the real build, not a hardcoded string
        // ==========================================================
        SettingsCard(
            kicker = "About",
            title = "AniRecap Studio",
            subtitle = "AI recap script + CapCut roadmap generator"
        ) {
            StudioKeyValueRow(
                label = "Version",
                value = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            )
            StudioKeyValueRow(
                label = "Build type",
                value = if (BuildConfig.DEBUG) "Debug" else "Release",
                valueColor = if (BuildConfig.DEBUG) WarningAmber else SuccessGreen
            )
            StudioKeyValueRow(
                label = "Last AI model used",
                value = if (useCustomProvider) openRouterModel else "Gemini (system key)"
            )
            StudioKeyValueRow(
                label = "API keys stored",
                value = "${apiKeys.size} on this device"
            )

            StudioDivider()

            Text(
                text = "កម្មវិធីបង្កើត Script សម្រាយសាច់រឿងភាសាខ្មែរតាមក្បួនខ្នាត Master Prompt (Plot-First 80% Story + 20% Recap + Rank/Faction Tracking) និងបង្កើត CapCut Editing Roadmaps ដោយស្វ័យប្រវតតិតាមរយ Gemini និង OpenRouter។",
                fontSize = 11.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // ==========================================================
    // Destructive action gets a real confirmation, with the true size
    // ==========================================================
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            icon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = ErrorRed) },
            title = { Text("សម្អាត Cache?") },
            text = {
                Text(
                    text = "This removes ${formatBytes(cache?.totalBytes ?: 0L)} inside the cache folders only. " +
                        "Scripts, API keys and preferences stay untouched. មិនលុប Script ឬ API Key ទេ។",
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showClearCacheDialog = false
                    lastCleanup = null
                    isClearingCache = true
                    scope.launch {
                        val result = CacheStats.clear(context)
                        lastCleanup = result
                        isClearingCache = false
                        cacheMeasureToken++
                        Toast.makeText(
                            context,
                            if (result.filesDeleted > 0) {
                                "Freed ${formatBytes(result.bytesFreed)}"
                            } else {
                                "Cache already empty"
                            },
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(
                        text = "Clear ${formatBytes(cache?.totalBytes ?: 0L)}",
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text(
                        text = "Cancel",
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        )
    }

    // Modal Dialog: Add API Key
    if (showAddKeyDialog) {
        AlertDialog(
            onDismissRequest = { showAddKeyDialog = false },
            title = { Text("Add new API key", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newKeyLabel,
                        onValueChange = { newKeyLabel = it },
                        label = { Text("Label") },
                        placeholder = { Text("e.g. My Gemini Pro Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(StudioStyle.innerRadius)
                    )
                    OutlinedTextField(
                        value = newKeyValue,
                        onValueChange = { newKeyValue = it },
                        label = { Text("API key value") },
                        placeholder = { Text("AIzaSy...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(StudioStyle.innerRadius)
                    )
                    StudioSegmentedControl(
                        options = listOf(
                            StudioSegment("Google Gemini", Icons.Default.AutoAwesome),
                            StudioSegment("OpenRouter", Icons.Default.Cloud)
                        ),
                        selectedIndex = if (newKeyProvider == "OpenRouter") 1 else 0,
                        onSelect = { index ->
                            newKeyProvider = if (index == 0) "Google Gemini" else "OpenRouter"
                        }
                    )
                    if (newKeyValue.isBlank()) {
                        Text(
                            text = "Paste a key first — saving an empty key is not allowed.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = newKeyValue.isNotBlank(),
                    onClick = {
                        AppSettings.addApiKey(newKeyLabel, newKeyValue, newKeyProvider)
                        showAddKeyDialog = false
                        newKeyLabel = ""
                        newKeyValue = ""
                        Toast.makeText(context, "បានរក្សាទុក API Key ថ្មី", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(
                        text = "Save key",
                        color = if (newKeyValue.isNotBlank()) CrimsonRedBright else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddKeyDialog = false }) {
                    Text(
                        text = "Cancel",
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        )
    }
}

// ==========================================================================
// Building blocks for this page
// ==========================================================================
@Composable
private fun SettingsCard(
    kicker: String,
    title: String,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    StudioCard(verticalSpacing = 12.dp) {
        StudioSectionHeader(
            kicker = kicker,
            title = title,
            subtitle = subtitle,
            trailing = trailing
        )
        content()
    }
}

/** Never prints the raw secret — first 4 + last 4 characters only. */
private fun com.example.data.ApiKeyEntry.maskedKey(): String =
    if (key.length > 8) key.take(4) + "••••••••" + key.takeLast(4) else "••••••••"
