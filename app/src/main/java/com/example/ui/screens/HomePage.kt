package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.AppLanguage
import com.example.data.AppSettings
import com.example.ui.AniRecapStudioViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    studioViewModel: AniRecapStudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentAppLang by AppSettings.language.collectAsState()
    val isKhmer = currentAppLang == AppLanguage.KHMER

    val videoUri by studioViewModel.videoUri.collectAsState()
    val videoFileName by studioViewModel.videoFileName.collectAsState()
    val videoFileSize by studioViewModel.videoFileSize.collectAsState()
    val animeOrMovieName by studioViewModel.animeOrMovieName.collectAsState()
    val episodes by studioViewModel.episodes.collectAsState()
    val sourceLanguage by studioViewModel.sourceLanguage.collectAsState()
    val targetLanguage by studioViewModel.targetLanguage.collectAsState()
    val selectedModel by studioViewModel.selectedModel.collectAsState()
    val isAnalyzing by studioViewModel.isAnalyzing.collectAsState()
    val progressMessage by studioViewModel.analysisProgressMessage.collectAsState()

    // File picker launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val displayName = uri.lastPathSegment?.substringAfterLast('/') ?: "uploaded_video.mp4"
            studioViewModel.onVideoPicked(uri, displayName, "Selected from Device")
        }
    }

    // Dropdown expansion states
    var sourceLangExpanded by remember { mutableStateOf(false) }
    var targetLangExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }

    val sourceLangOptions = listOf("Auto Detect", "Japanese (日本語)", "Korean (한국어)", "Chinese (中文)", "English")
    val targetLangOptions = listOf("Khmer & English target", "Khmer (ភាសាខ្មែរ)", "English")
    val modelOptions = listOf("Gemini 3.8", "Gemini 3.7", "Gemini 3.6", "Gemini 3.5")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title Banner
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ani",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = CrimsonRedBright
                )
                Text(
                    text = "Recap Studio",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = AuraCyan
                )
            }
            Text(
                text = if (isKhmer) "ប្រព័ន្ធស្វ័យប្រវត្តិកាត់ត និងបង្កើត Script សម្រាយសាច់រឿងជាមួយ Gemini" 
                       else "Automated Anime & Movie Recap Analysis with Gemini Models",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 1: Upload Video Box
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { videoPickerLauncher.launch("video/*") }
                .testTag("upload_video_box"),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (videoUri != null) CrimsonRedBright else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CrimsonRedBright.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (videoUri != null) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                        contentDescription = "Upload Video",
                        tint = if (videoUri != null) CrimsonRedBright else AuraCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = if (videoUri != null) "វីដេអូបានជ្រើសរើសរួចរាល់ (Video Selected)" else "Upload Video (ជ្រើសរើសវីដេអូ)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = CrimsonRedBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$videoFileName • $videoFileSize",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "ចុចទីនេះដើម្បី Upload ពីទូរស័ព្ទ (MP4, MKV, AVI)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 2: Movie/Anime Name & Episodes []
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = animeOrMovieName,
                onValueChange = { studioViewModel.animeOrMovieName.value = it },
                label = { Text("Movie/Anime Name") },
                placeholder = { Text("Solo Leveling, JJK...") },
                modifier = Modifier
                    .weight(1.4f)
                    .testTag("input_anime_name"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = episodes,
                onValueChange = { studioViewModel.episodes.value = it },
                label = { Text("Episodes []") },
                placeholder = { Text("EP 1 - 12") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_episodes"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        // Section 3: Default Language Auto detect
        ExposedDropdownMenuBox(
            expanded = sourceLangExpanded,
            onExpandedChange = { sourceLangExpanded = !sourceLangExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = sourceLanguage,
                onValueChange = {},
                readOnly = true,
                label = { Text("Default Language (ភាសាដើម)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceLangExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("dropdown_source_language"),
                shape = RoundedCornerShape(10.dp)
            )

            ExposedDropdownMenu(
                expanded = sourceLangExpanded,
                onDismissRequest = { sourceLangExpanded = false }
            ) {
                sourceLangOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            studioViewModel.sourceLanguage.value = option
                            sourceLangExpanded = false
                        }
                    )
                }
            }
        }

        // Section 4: Target Language (Khmer & English target)
        ExposedDropdownMenuBox(
            expanded = targetLangExpanded,
            onExpandedChange = { targetLangExpanded = !targetLangExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = targetLanguage,
                onValueChange = {},
                readOnly = true,
                label = { Text("Target Language (ភាសាសម្រាយ)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetLangExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("dropdown_target_language"),
                shape = RoundedCornerShape(10.dp)
            )

            ExposedDropdownMenu(
                expanded = targetLangExpanded,
                onDismissRequest = { targetLangExpanded = false }
            ) {
                targetLangOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            studioViewModel.targetLanguage.value = option
                            targetLangExpanded = false
                        }
                    )
                }
            }
        }

        // Section 5: Models select drop-down: Gemini: 3.5 3.6 3.7 3.8
        ExposedDropdownMenuBox(
            expanded = modelExpanded,
            onExpandedChange = { modelExpanded = !modelExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "Gemini: $selectedModel",
                onValueChange = {},
                readOnly = true,
                label = { Text("Models select (ជ្រើសរើស Model)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("dropdown_gemini_model"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ManaVioletLight,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            ExposedDropdownMenu(
                expanded = modelExpanded,
                onDismissRequest = { modelExpanded = false }
            ) {
                modelOptions.forEach { modelName ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Gemini $modelName", fontWeight = FontWeight.Bold)
                                if (modelName == "3.8") {
                                    Surface(
                                        color = CrimsonRedBright.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "RECOMMENDED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CrimsonRedBright,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            studioViewModel.selectedModel.value = modelName
                            modelExpanded = false
                        }
                    )
                }
            }
        }

        // Section 6: Analyze Action Button
        Button(
            onClick = { studioViewModel.runAnalyze() },
            enabled = !isAnalyzing,
            colors = ButtonDefaults.buttonColors(
                containerColor = CrimsonRedBright
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("analyze_button")
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Analyzing with $selectedModel...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyze (វិភាគ និងបង្កើត Recap)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Progress Message display
        AnimatedVisibility(visible = isAnalyzing) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = AuraCyan
                    )
                    Text(
                        text = progressMessage,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
