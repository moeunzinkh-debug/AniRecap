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
    val videoDurationSeconds by studioViewModel.videoDurationSeconds.collectAsState()
    val animeOrMovieName by studioViewModel.animeOrMovieName.collectAsState()
    val episodes by studioViewModel.episodes.collectAsState()
    val sourceLanguage by studioViewModel.sourceLanguage.collectAsState()
    val targetLanguage by studioViewModel.targetLanguage.collectAsState()
    val selectedModel by studioViewModel.selectedModel.collectAsState()
    val isAnalyzing by studioViewModel.isAnalyzing.collectAsState()
    val progressMessage by studioViewModel.analysisProgressMessage.collectAsState()
    val errorMessage by studioViewModel.errorMessage.collectAsState()

    // File picker launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val displayName = uri.lastPathSegment?.substringAfterLast('/') ?: "uploaded_video.mp4"
            val sizeStr = try {
                val sizeBytes = context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: 0L
                formatFileSize(sizeBytes)
            } catch (e: Exception) {
                "Unknown size"
            }
            studioViewModel.onVideoPicked(uri, displayName, sizeStr)
        }
    }

    // Dropdown expansion states
    var sourceLangExpanded by remember { mutableStateOf(false) }
    var targetLangExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }

    val sourceLangOptions = listOf("Auto Detect", "Japanese", "Korean", "Chinese", "English")
    val targetLangOptions = listOf("Khmer & English target", "Khmer", "English")
    val modelOptions = listOf("Gemini 3.8", "Gemini 3.7", "Gemini 3.6", "Gemini 3.5")

    // Validation: can only analyze with a real video + name
    val canAnalyze = videoUri != null && animeOrMovieName.isNotBlank() && !isAnalyzing

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
                text = if (isKhmer) "AI Video Analysis - creates recap scripts from your uploaded video"
                       else "Real AI-powered Anime & Movie Recap Analysis from YOUR uploaded video",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Error message display
        AnimatedVisibility(visible = errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = ErrorRed,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { studioViewModel.clearError() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = ErrorRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
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
                if (videoUri != null) SuccessGreen else MaterialTheme.colorScheme.outlineVariant
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
                        .background(
                            if (videoUri != null) SuccessGreen.copy(alpha = 0.15f)
                            else AuraCyan.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (videoUri != null) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                        contentDescription = "Upload Video",
                        tint = if (videoUri != null) SuccessGreen else AuraCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = if (videoUri != null) "Video Selected" else "Upload Your Video",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (videoUri != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = CrimsonRedBright, modifier = Modifier.size(16.dp))
                                Text(videoFileName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            if (videoFileSize.isNotBlank()) {
                                Text(videoFileSize, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (videoDurationSeconds > 0) {
                                Text("Duration: ${formatDuration(videoDurationSeconds)}", fontSize = 11.sp, color = AuraCyan, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                } else {
                    Text("No video selected. Tap to upload your real video file.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(
                    text = if (videoUri != null) "Tap to change video" else "Tap to select video from your device (MP4, MKV, AVI)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 2: Movie/Anime Name & Episodes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = animeOrMovieName,
                onValueChange = { studioViewModel.animeOrMovieName.value = it },
                label = { Text("Movie/Anime Name *") },
                placeholder = { Text("Enter the actual name...") },
                modifier = Modifier.weight(1.4f).testTag("input_anime_name"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                isError = animeOrMovieName.isBlank() && errorMessage != null
            )

            OutlinedTextField(
                value = episodes,
                onValueChange = { studioViewModel.episodes.value = it },
                label = { Text("Episodes") },
                placeholder = { Text("EP 1 - 12") },
                modifier = Modifier.weight(1f).testTag("input_episodes"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        // Section 3: Source Language
        ExposedDropdownMenuBox(
            expanded = sourceLangExpanded,
            onExpandedChange = { sourceLangExpanded = !sourceLangExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = sourceLanguage,
                onValueChange = {},
                readOnly = true,
                label = { Text("Source Language (from video)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceLangExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("dropdown_source_language"),
                shape = RoundedCornerShape(10.dp)
            )
            ExposedDropdownMenu(expanded = sourceLangExpanded, onDismissRequest = { sourceLangExpanded = false }) {
                sourceLangOptions.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        studioViewModel.sourceLanguage.value = option; sourceLangExpanded = false
                    })
                }
            }
        }

        // Section 4: Target Language
        ExposedDropdownMenuBox(
            expanded = targetLangExpanded,
            onExpandedChange = { targetLangExpanded = !targetLangExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = targetLanguage,
                onValueChange = {},
                readOnly = true,
                label = { Text("Target Language") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetLangExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("dropdown_target_language"),
                shape = RoundedCornerShape(10.dp)
            )
            ExposedDropdownMenu(expanded = targetLangExpanded, onDismissRequest = { targetLangExpanded = false }) {
                targetLangOptions.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        studioViewModel.targetLanguage.value = option; targetLangExpanded = false
                    })
                }
            }
        }

        // Section 5: Model select
        ExposedDropdownMenuBox(
            expanded = modelExpanded,
            onExpandedChange = { modelExpanded = !modelExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "Gemini: $selectedModel",
                onValueChange = {},
                readOnly = true,
                label = { Text("AI Model") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("dropdown_gemini_model"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ManaVioletLight,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            ExposedDropdownMenu(expanded = modelExpanded, onDismissRequest = { modelExpanded = false }) {
                modelOptions.forEach { modelName ->
                    DropdownMenuItem(
                        text = {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Text("Gemini $modelName", fontWeight = FontWeight.Bold)
                                if (modelName == "3.8") {
                                    Surface(color = CrimsonRedBright.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text("RECOMMENDED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CrimsonRedBright, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        },
                        onClick = { studioViewModel.selectedModel.value = modelName; modelExpanded = false }
                    )
                }
            }
        }

        // Section 6: Analyze Action Button
        Button(
            onClick = { studioViewModel.runAnalyze() },
            enabled = canAnalyze,
            colors = ButtonDefaults.buttonColors(
                containerColor = CrimsonRedBright,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(54.dp).testTag("analyze_button")
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                Spacer(Modifier.width(12.dp))
                Text("Analyzing your video with $selectedModel...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (canAnalyze) "Analyze My Video" else "Upload video + enter name to start", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (!canAnalyze && !isAnalyzing) {
            Text("Step 1: Upload your video. Step 2: Enter the anime/movie name. Step 3: Tap Analyze.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
        }

        // Progress Message display
        AnimatedVisibility(visible = isAnalyzing) {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = AuraCyan)
                    Text(progressMessage, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "%.1f KB".format(kb)
    val mb = kb / 1024.0
    if (mb < 1024) return "%.1f MB".format(mb)
    val gb = mb / 1024.0
    return "%.2f GB".format(gb)
}

private fun formatDuration(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%d:%02d".format(min, sec)
}
