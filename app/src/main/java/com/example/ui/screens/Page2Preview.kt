package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.example.model.CapCutMarker
import com.example.model.GeneratedRecapScript
import com.example.model.RecapProject
import com.example.ui.AniRecapStudioViewModel
import com.example.ui.StudioNavScreen
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun Page2Preview(
    studioViewModel: AniRecapStudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val project by studioViewModel.activeProject.collectAsState()

    var isPlaying by remember { mutableStateOf(false) }
    var currentSeconds by remember { mutableIntStateOf(15) }
    val totalSeconds = 600 // 10 minutes

    var selectedAspect16_9 by remember { mutableStateOf(true) } // true: 16:9, false: 9:16
    var activeTab by remember { mutableIntStateOf(0) } // 0: Script new story Recaps, 1: Guide cut Recap for Capcut

    var isEditingScript by remember { mutableStateOf(false) }
    var editableScriptText by remember { mutableStateOf("") }

    LaunchedEffect(project) {
        project?.script?.let {
            editableScriptText = it.fullFormattedNarration
        }
    }

    // Playback loop simulation
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            if (currentSeconds < totalSeconds) {
                currentSeconds++
            } else {
                isPlaying = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Page 2: Monitor & Results",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${project?.animeOrMovieName ?: "Recap"} • ${project?.episodes ?: ""}",
                    fontSize = 12.sp,
                    color = ManaVioletLight
                )
            }

            // Aspect ratio toggle
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    color = if (selectedAspect16_9) CrimsonRedBright else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { selectedAspect16_9 = true }
                ) {
                    Text(
                        text = "16:9 YT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedAspect16_9) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = if (!selectedAspect16_9) CrimsonRedBright else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { selectedAspect16_9 = false }
                ) {
                    Text(
                        text = "9:16 TikTok",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!selectedAspect16_9) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // ==========================================
        // Video monitor preview
        // ----------------
        // -              -
        // ________________
        // ==========================================
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(if (selectedAspect16_9) 210.dp else 290.dp)
                .testTag("video_monitor_preview")
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Monitor Grid and Frame simulation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Monitor Bar: REC ●, Resolution, Model
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) ErrorRed else TextTertiary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MONITOR PREVIEW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "${project?.selectedModel ?: "Gemini 3.8"} • 1080P",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuraCyan
                        )
                    }

                    // Center Play/Action Focus & Framing
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(52.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Monitor Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Bottom Monitor Controls & Timecode
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Slider(
                            value = currentSeconds.toFloat(),
                            onValueChange = { currentSeconds = it.toInt() },
                            valueRange = 0f..totalSeconds.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = CrimsonRedBright,
                                activeTrackColor = CrimsonRedBright,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val curMin = currentSeconds / 60
                            val curSec = currentSeconds % 60
                            val totMin = totalSeconds / 60
                            val totSec = totalSeconds % 60
                            Text(
                                text = String.format("%02d:%02d / %02d:%02d", curMin, curSec, totMin, totSec),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "CapCut Cut Sync: Frame ${currentSeconds * 30}",
                                color = GoldLegendary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Section Tabs: "Script new story Recaps" vs "Guide cut Recap for Capcut"
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = CrimsonRedBright,
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        "Script new story Recaps",
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_script_recaps")
            )

            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        "Guide cut Recap for Capcut",
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_guide_capcut")
            )
        }

        // ==========================================
        // Tab 0: Script new story Recaps
        // ==========================================
        if (activeTab == 0) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Action row: Copy, Edit, Re-generate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Script សម្រាយសាច់រឿង (Master Format)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = {
                                if (isEditingScript) {
                                    studioViewModel.updateScriptContent(editableScriptText)
                                    isEditingScript = false
                                    Toast.makeText(context, "បានរក្សាទុក Script រួចរាល់", Toast.LENGTH_SHORT).show()
                                } else {
                                    isEditingScript = true
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditingScript) Icons.Default.Save else Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isEditingScript) "Save" else "Edit", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("AniRecap Script", editableScriptText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "ចម្លង Script រួចរាល់! (Copied to Clipboard)", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRedBright),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("copy_script_button")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Script", fontSize = 11.sp)
                        }
                    }
                }

                if (isEditingScript) {
                    OutlinedTextField(
                        value = editableScriptText,
                        onValueChange = { editableScriptText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 350.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {
                    project?.script?.let { script ->
                        // Hook Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🔥 Hook ចំណុចទាក់ទាញដំបូង:",
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonRedBright,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "“${script.hook}”",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Full Narration Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "ខ្លឹមសារនិទានសម្រាយ (Plot-First 80% Story + 20% Recap):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraCyan
                                )
                                Text(
                                    text = script.fullFormattedNarration,
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // Tab 1: Guide cut Recap for Capcut
        // ==========================================
        if (activeTab == 1) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Header & Copy Guide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CapCut Cut Roadmaps",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Timestamp, Speed, SFX, និងគន្លឹះកាត់តក្នុង CapCut",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            val guideText = buildString {
                                appendLine("=== CAPCUT CUT GUIDE: ${project?.animeOrMovieName} ===")
                                project?.capCutGuide?.forEach {
                                    appendLine("[Cut #${it.cutIndex}] ${it.timeRange} (${it.durationText})")
                                    appendLine("Scene: ${it.actionScene}")
                                    appendLine("Speed: ${it.speedMultiplier} | Transition: ${it.transition} | SFX: ${it.sfxEffect}")
                                    appendLine("Voiceover: ${it.voiceoverPromptKhmer}")
                                    appendLine("CapCut Tip: ${it.capCutEditorTip}")
                                    appendLine("----------------------------------------")
                                }
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("CapCut Cut Guide", guideText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "ចម្លង CapCut Cut Guide រួចរាល់!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ManaViolet),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("copy_capcut_guide_button")
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Markers", fontSize = 11.sp)
                    }
                }

                // CapCut Recommendations Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BGM Recommendation: ${project?.bgmRecommendation}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AspectRatio, contentDescription = null, tint = GoldLegendary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CapCut Setting: ${project?.capCutTemplateAdvice}", fontSize = 12.sp)
                        }
                    }
                }

                // List of CapCut Cut markers
                project?.capCutGuide?.forEach { marker ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Cut number & Time Range
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = CrimsonRedBright.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "CUT #${marker.cutIndex} • ${marker.timeRange}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonRedBright,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = marker.durationText,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "ឈុតឆាក: ${marker.actionScene}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Speed, Transition, SFX badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                MarkerBadge(label = "Speed: ${marker.speedMultiplier}", color = AuraCyan)
                                MarkerBadge(label = "Effect: ${marker.transition}", color = ManaVioletLight)
                                MarkerBadge(label = "SFX: ${marker.sfxEffect}", color = GoldLegendary)
                            }

                            // Voiceover Prompt
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🎤 Voiceover: “${marker.voiceoverPromptKhmer}”",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            // CapCut Specific Pro Tip
                            Text(
                                text = "💡 CapCut Editor Tip: ${marker.capCutEditorTip}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarkerBadge(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
