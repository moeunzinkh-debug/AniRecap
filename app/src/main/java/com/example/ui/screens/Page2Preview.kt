package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CapCutMarker
import com.example.model.RecapProject
import com.example.ui.AniRecapStudioViewModel
import com.example.ui.StudioNavScreen
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioDivider
import com.example.ui.components.StudioEmptyBlock
import com.example.ui.components.StudioInfoRow
import com.example.ui.components.StudioKicker
import com.example.ui.components.StudioMetaChip
import com.example.ui.components.StudioOutlineButton
import com.example.ui.components.StudioPrimaryButton
import com.example.ui.components.StudioQuoteBlock
import com.example.ui.components.StudioSegment
import com.example.ui.components.StudioSegmentedControl
import com.example.ui.components.StudioSecondaryButton
import com.example.ui.components.StudioSectionHeader
import com.example.ui.components.StudioStatusPill
import com.example.ui.components.StudioStyle
import com.example.ui.components.StudioTag
import com.example.ui.components.StudioTextButton
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.CrimsonRedBright
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldLegendary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/** Export target chosen from the header dropdown (replaces the old chip cluster). */
private enum class StudioFormat(
    val menuLabel: String,
    val shortLabel: String,
    val hint: String,
    val frameRatio: Float
) {
    Wide("16:9 · YouTube", "16:9", "Widescreen master for long-form recap uploads.", 16f / 9f),
    Vertical("9:16 · TikTok / Shorts", "9:16", "Vertical crop — use CapCut Smart Auto Reframe.", 9f / 16f),
    Square("1:1 · Feed preview", "1:1", "Square safe area for feed and thumbnail tests.", 1f)
}

private const val TIMELINE_FPS = 30

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Page2Preview(
    studioViewModel: AniRecapStudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val project by studioViewModel.activeProject.collectAsState()
    val isPlaying by studioViewModel.isMonitorPlaying.collectAsState()
    val positionSeconds by studioViewModel.monitorPositionSeconds.collectAsState()
    val thumbnail by studioViewModel.videoThumbnail.collectAsState()

    var format by remember { mutableStateOf(StudioFormat.Wide) }
    var activeTab by rememberSaveable { mutableIntStateOf(0) }
    var isEditingScript by remember { mutableStateOf(false) }
    var draftScript by remember { mutableStateOf("") }

    val activeProject = project
    val totalSeconds = activeProject?.videoDurationSeconds?.takeIf { it > 0 } ?: 0
    val playhead = positionSeconds.coerceIn(0f, totalSeconds.coerceAtLeast(1).toFloat())
    val playheadFrame = (playhead * TIMELINE_FPS).roundToInt()

    LaunchedEffect(activeProject?.id) {
        draftScript = activeProject?.script?.fullFormattedNarration ?: ""
        isEditingScript = false
    }

    // Playback simulation, driven from the ViewModel so the playhead survives
    // tab switches.
    LaunchedEffect(isPlaying, totalSeconds) {
        while (isPlaying && totalSeconds > 0) {
            delay(100)
            val next = studioViewModel.monitorPositionSeconds.value + 0.1f
            if (next >= totalSeconds) {
                studioViewModel.seekMonitorTo(totalSeconds.toFloat())
                studioViewModel.stopMonitor()
                break
            }
            studioViewModel.seekMonitorTo(next)
        }
    }

    // ==========================================================
    // EMPTY STATE — nothing analysed yet (no fake/sample content)
    // ==========================================================
    if (activeProject == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.OndemandVideo,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Nothing on the monitor yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Upload a video on the Home tab and tap Analyze. The script and the CapCut cut list will appear here, timed to your real runtime.",
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            StudioPrimaryButton(
                text = "Go to Upload",
                icon = Icons.Default.CloudUpload,
                onClick = { studioViewModel.navigateTo(StudioNavScreen.HomeUpload) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        return
    }

    val script = activeProject.script
    val narration = script?.fullFormattedNarration.orEmpty()
    val cuts = activeProject.capCutGuide
    val activeCutIndex = cuts.indexOfFirst { marker ->
        val start = marker.startSeconds()
        val end = marker.endSeconds()
        start != null && playhead >= start && (end == null || playhead < end)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = StudioStyle.screenPadding)
                .padding(top = 18.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(StudioStyle.sectionGap)
        ) {
            // ==========================================================
            // HEADER — page label first, real title dominant, one format control
            // ==========================================================
            Row(verticalAlignment = Alignment.Top) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StudioKicker("Page 2 · Monitor & Results")
                    Text(
                        text = activeProject.animeOrMovieName.ifBlank { "Untitled Recap" },
                        fontSize = StudioStyle.pageTitleSize,
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                FormatSelector(
                    selected = format,
                    onSelect = { format = it }
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (activeProject.episodes.isNotBlank()) {
                    StudioMetaChip(text = activeProject.episodes, icon = Icons.Default.Subtitles)
                }
                if (totalSeconds > 0) {
                    StudioMetaChip(
                        text = "Runtime ${formatDuration(totalSeconds)}",
                        icon = Icons.Default.Schedule,
                        tint = AuraCyan
                    )
                }
                if (activeProject.videoFileSize.isNotBlank()) {
                    StudioMetaChip(
                        text = activeProject.videoFileSize,
                        icon = Icons.Default.Bolt,
                        tint = SuccessGreen
                    )
                }
                StudioMetaChip(
                    text = activeProject.selectedModel,
                    icon = Icons.Default.Speed,
                    tint = GoldLegendary
                )
            }

            // ==========================================================
            // MONITOR PREVIEW
            // ==========================================================
            MonitorPreviewCard(
                project = activeProject,
                thumbnail = thumbnail,
                format = format,
                isPlaying = isPlaying,
                playheadSeconds = playhead,
                totalSeconds = totalSeconds,
                playheadFrame = playheadFrame,
                inCutRange = activeCutIndex >= 0,
                onTogglePlay = { studioViewModel.toggleMonitorPlay() },
                onSeek = { studioViewModel.seekMonitorTo(it) }
            )

            // ==========================================================
            // TABS
            // ==========================================================
            StudioSegmentedControl(
                options = listOf(
                    StudioSegment("Recap Script", Icons.Default.Movie),
                    StudioSegment("CapCut Guide", Icons.Default.ContentCut)
                ),
                selectedIndex = activeTab,
                onSelect = { activeTab = it },
                modifier = Modifier.testTag("studio_tab_switch")
            )

            when (activeTab) {
                // ==========================================================
                // TAB 0 — narration script
                // ==========================================================
                0 -> Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(StudioStyle.cardGap)
                ) {
                    StudioSectionHeader(
                        kicker = "Master script",
                        title = "សម្រាយសាច់រឿង (plot-first narration)",
                        subtitle = if (narration.isNotBlank()) {
                            val words = narration.wordCount()
                            "${words} words · ~${formatDuration((words / 2.4f).roundToInt())} voice-over at 145 wpm"
                        } else {
                            "No narration generated for this video yet."
                        }
                    )

                    if (script == null) {
                        StudioEmptyBlock(
                            icon = Icons.Default.Movie,
                            title = "មិនទាន់មាន Script សម្រាយសាច់រឿងនៅឡើយទេ",
                            message = "Re-run the analysis from the Upload tab to generate the Khmer recap narration.",
                            actionLabel = "Back to Upload",
                            onAction = { studioViewModel.navigateTo(StudioNavScreen.HomeUpload) }
                        )
                    } else {
                        StudioCard(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            borderColor = Color.Transparent,
                            verticalSpacing = 6.dp
                        ) {
                            StudioKicker("ចំណុចទាក់ទាញដំបូង · Hook", color = CrimsonRedBright)
                            Text(
                                text = "\"${script.hook}\"",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        StudioCard {
                            StudioKicker("Full narration · ដំណើររឿង")
                            StudioDivider()
                            if (isEditingScript) {
                                OutlinedTextField(
                                    value = draftScript,
                                    onValueChange = { draftScript = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 300.dp),
                                    shape = RoundedCornerShape(StudioStyle.innerRadius),
                                    label = { Text("Edit the narration") },
                                    supportingText = {
                                        Text(
                                            "Saved on this device only — the AI output is not overwritten.",
                                            fontSize = 11.sp
                                        )
                                    }
                                )
                            } else {
                                SelectionContainer {
                                    Text(
                                        text = narration,
                                        fontSize = 15.sp,
                                        lineHeight = 27.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================================
                // TAB 1 — CapCut cut roadmap
                // ==========================================================
                1 -> Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(StudioStyle.cardGap)
                ) {
                    StudioSectionHeader(
                        kicker = "CapCut editing roadmap",
                        title = "${cuts.size} cut markers · ${formatDuration(totalSeconds)}",
                        subtitle = "Timestamps follow the real runtime of your video. Tap a cut to jump the monitor to its in-point."
                    )

                    StudioCard {
                        StudioInfoRow(
                            icon = Icons.Default.GraphicEq,
                            label = "BGM recommendation",
                            value = activeProject.bgmRecommendation,
                            accent = AuraCyan
                        )
                        StudioDivider()
                        StudioInfoRow(
                            icon = Icons.Default.AspectRatio,
                            label = "Format & reframe",
                            value = "${format.menuLabel} — ${activeProject.capCutTemplateAdvice}",
                            accent = GoldLegendary
                        )
                    }

                    if (cuts.isEmpty()) {
                        StudioEmptyBlock(
                            icon = Icons.Default.ContentCut,
                            title = "No cut markers yet",
                            message = "Once the analysis finishes, every cut gets a timestamp, speed note and transition suggestion here."
                        )
                    } else {
                        cuts.forEachIndexed { index, marker ->
                            CapCutCutCard(
                                marker = marker,
                                isLive = index == activeCutIndex,
                                onJump = { start ->
                                    studioViewModel.jumpToCut(start)
                                    Toast.makeText(
                                        context,
                                        "Monitor jumped to ${formatTimecode(start)}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }

        // ==========================================================
        // STICKY ACTION BAR — real buttons, rounded rectangles, one line
        // ==========================================================
        StudioBottomBar {
            if (activeTab == 0) {
                if (isEditingScript) {
                    StudioTextButton(
                        text = "Cancel",
                        onClick = {
                            draftScript = narration
                            isEditingScript = false
                        }
                    )
                    StudioPrimaryButton(
                        text = "Save changes",
                        icon = Icons.Default.Save,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            studioViewModel.updateScriptContent(draftScript)
                            isEditingScript = false
                            Toast.makeText(context, "Script saved", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    StudioOutlineButton(
                        text = "Edit script",
                        icon = Icons.Default.Edit,
                        modifier = Modifier.weight(1f),
                        enabled = script != null,
                        onClick = { isEditingScript = true }
                    )
                    StudioPrimaryButton(
                        text = "Copy script",
                        icon = Icons.Default.ContentCopy,
                        modifier = Modifier.weight(1f),
                        enabled = narration.isNotBlank(),
                        onClick = {
                            context.copyToClipboard("AniRecap script", narration)
                            Toast.makeText(context, "Script copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            } else {
                StudioSecondaryButton(
                    text = "Copy markers",
                    icon = Icons.Default.List,
                    modifier = Modifier.weight(1f),
                    enabled = cuts.isNotEmpty(),
                    onClick = {
                        context.copyToClipboard("CapCut markers", buildMarkerList(activeProject, format))
                        Toast.makeText(context, "Marker list copied", Toast.LENGTH_SHORT).show()
                    }
                )
                StudioPrimaryButton(
                    text = "Copy full guide",
                    icon = Icons.Default.ContentCopy,
                    modifier = Modifier.weight(1f),
                    enabled = cuts.isNotEmpty(),
                    onClick = {
                        context.copyToClipboard("CapCut guide", buildFullGuide(activeProject, format))
                        Toast.makeText(context, "Full CapCut guide copied", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

// ==========================================================================
// HEADER FORMAT DROPDOWN
// ==========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormatSelector(
    selected: StudioFormat,
    onSelect: (StudioFormat) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = true },
        shape = RoundedCornerShape(StudioStyle.innerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.testTag("studio_format_selector")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.End) {
                StudioKicker("Format")
                Text(
                    text = selected.shortLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Choose export format",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(20.dp)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        StudioFormat.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(
                            text = option.menuLabel,
                            fontSize = 13.sp,
                            fontWeight = if (option == selected) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = option.hint,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                trailingIcon = {
                    if (option == selected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = CrimsonRedBright,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                onClick = {
                    onSelect(option)
                    expanded = false
                }
            )
        }
    }
}

// ==========================================================================
// MONITOR PREVIEW CARD
// ==========================================================================
@Composable
private fun MonitorPreviewCard(
    project: RecapProject,
    thumbnail: Bitmap?,
    format: StudioFormat,
    isPlaying: Boolean,
    playheadSeconds: Float,
    totalSeconds: Int,
    playheadFrame: Int,
    inCutRange: Boolean,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit
) {
    val stageShape = RoundedCornerShape(StudioStyle.innerRadius)

    StudioCard(
        containerColor = Color.Black,
        borderColor = MaterialTheme.colorScheme.outlineVariant,
        contentPadding = 14.dp,
        verticalSpacing = 12.dp,
        modifier = Modifier.testTag("video_monitor_preview")
    ) {
        // Status strip
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
                        .background(if (isPlaying) ErrorRed else TextPrimary.copy(alpha = 0.4f))
                )
                Spacer(modifier = Modifier.width(6.dp))
                StudioKicker(if (isPlaying) "Monitor · playing" else "Monitor · paused")
            }
            Text(
                text = "${format.shortLabel} · ${TIMELINE_FPS} fps",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AuraCyan,
                maxLines = 1
            )
        }

        // Stage: real poster frame when available, otherwise a labelled placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(stageShape)
                .background(Color(0xFF07080D))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .aspectRatio(format.frameRatio, matchHeightConstraintsFirst = true)
                    .background(Color.Black)
            ) {
                if (thumbnail != null) {
                    Image(
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = "Frame from ${project.videoFileName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AuraCyan.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.OndemandVideo,
                                contentDescription = null,
                                tint = AuraCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No poster frame available",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Timecode and cut markers still track your video.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = TextPrimary.copy(alpha = 0.55f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Legibility scrim + overlay strip
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Black.copy(alpha = 0.55f),
                                0.35f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.7f)
                            )
                        )
                )

                Text(
                    text = project.videoFileName.ifBlank { "source_video" },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary.copy(alpha = 0.85f),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                        .clickable(onClick = onTogglePlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause monitor" else "Play monitor",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "${totalSeconds * TIMELINE_FPS} frames · ${formatDuration(totalSeconds)} source",
                    fontSize = 10.sp,
                    color = TextPrimary.copy(alpha = 0.6f),
                    maxLines = 1,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }
        }

        // Timecode + CapCut sync status, justified to opposite ends
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatTimecode(playheadSeconds.toInt()),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary
                )
                Text(
                    text = " / ${formatTimecode(totalSeconds)}",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary.copy(alpha = 0.5f)
                )
            }

            if (inCutRange) {
                StudioStatusPill(text = "Synced · frame $playheadFrame", color = SuccessGreen)
            } else {
                StudioStatusPill(
                    text = "Frame $playheadFrame",
                    color = WarningAmber,
                    icon = Icons.Default.Timer
                )
            }
        }

        Slider(
            value = playheadSeconds,
            onValueChange = onSeek,
            valueRange = 0f..totalSeconds.coerceAtLeast(1).toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = CrimsonRedBright,
                activeTrackColor = CrimsonRedBright,
                inactiveTrackColor = TextPrimary.copy(alpha = 0.22f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
    }
}

// ==========================================================================
// CUT CARD — time block / action block / spec chips, content-height
// ==========================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CapCutCutCard(
    marker: CapCutMarker,
    isLive: Boolean,
    onJump: (Int) -> Unit
) {
    StudioCard(
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = if (isLive) CrimsonRedBright.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outlineVariant,
        verticalSpacing = 10.dp,
        onClick = { marker.startSeconds()?.let(onJump) },
        modifier = Modifier.testTag("capcut_cut_${marker.cutIndex}")
    ) {
        // Block 1 — time
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = CrimsonRedBright.copy(alpha = 0.14f),
                shape = RoundedCornerShape(StudioStyle.chipRadius),
                contentColor = CrimsonRedBright
            ) {
                Text(
                    text = "CUT %02d".format(marker.cutIndex),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = marker.timeRange,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = marker.durationText,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Block 2 — scene / action (Khmer)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            StudioKicker("Scene · សកម្មភាព")
            Text(
                text = marker.actionScene,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 26.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Block 3 — technical specs
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StudioTag(label = "Speed", value = marker.speedMultiplier)
            StudioTag(label = "Transition", value = marker.transition)
            StudioTag(label = "SFX", value = marker.sfxEffect)
        }

        // Block 4 — voice-over line
        StudioQuoteBlock(
            label = "Voiceover · សម្លេងអាន",
            text = "\"${marker.voiceoverPromptKhmer}\""
        )

        // Block 5 — editor tip
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(14.dp)
                    .padding(top = 1.dp)
            )
            Text(
                text = marker.capCutEditorTip,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================================================
// STICKY ACTION BAR
// ==========================================================================
@Composable
private fun StudioBottomBar(content: @Composable RowScope.() -> Unit) {
    val shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, shape, clip = false)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape)
            .padding(horizontal = StudioStyle.screenPadding, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

// ==========================================================================
// HELPERS
// ==========================================================================
private val FirstTimestamp = Regex("""(\d{1,2}):(\d{2})(?::(\d{2}))?""")

/** "MM:SS - MM:SS" in seconds; tolerant of the formats the model returns. */
private fun CapCutMarker.startSeconds(): Int? = FirstTimestamp.find(timeRange)?.toSeconds()

private fun CapCutMarker.endSeconds(): Int? {
    val range = timeRange.split('-', '\u2013', '\u2014')
    if (range.size < 2) return null
    return FirstTimestamp.find(range[1])?.toSeconds()
}

private fun MatchResult.toSeconds(): Int? {
    val first = groupValues.getOrNull(1)?.toIntOrNull() ?: return null
    val second = groupValues.getOrNull(2)?.toIntOrNull() ?: return null
    val third = groupValues.getOrNull(3)?.toIntOrNull()
    // "MM:SS" when there are two parts, "HH:MM:SS" when there are three.
    return if (third != null) first * 3600 + second * 60 + third else first * 60 + second
}

private fun String.wordCount(): Int = split(Regex("\\s+")).count { it.isNotBlank() }

private fun Context.copyToClipboard(label: String, text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}

private fun buildMarkerList(project: RecapProject, format: StudioFormat): String =
    buildString {
        appendLine("# CapCut markers · ${project.animeOrMovieName} · ${format.shortLabel}")
        project.capCutGuide.forEach { marker ->
            appendLine("${marker.timeRange}  cut ${marker.cutIndex}  ${marker.speedMultiplier}  ${marker.transition}  ${marker.sfxEffect}")
        }
    }

private fun buildFullGuide(project: RecapProject, format: StudioFormat): String =
    buildString {
        appendLine("CAPCUT CUT GUIDE — ${project.animeOrMovieName}")
        if (project.episodes.isNotBlank()) appendLine("Episodes: ${project.episodes}")
        appendLine("Source: ${project.videoFileName} (${formatDuration(project.videoDurationSeconds)}) · ${format.menuLabel}")
        appendLine("BGM: ${project.bgmRecommendation}")
        appendLine("Format advice: ${project.capCutTemplateAdvice}")
        appendLine()
        project.capCutGuide.forEach { marker ->
            appendLine("CUT ${marker.cutIndex} · ${marker.timeRange} · ${marker.durationText}")
            appendLine("  Scene: ${marker.actionScene}")
            appendLine("  Speed: ${marker.speedMultiplier} | Transition: ${marker.transition} | SFX: ${marker.sfxEffect}")
            appendLine("  Voiceover: ${marker.voiceoverPromptKhmer}")
            appendLine("  Tip: ${marker.capCutEditorTip}")
            appendLine()
        }
    }

private fun formatDuration(seconds: Int): String {
    if (seconds <= 0) return "0:00"
    val min = seconds / 60
    val sec = seconds % 60
    return "%d:%02d".format(min, sec)
}

private fun formatTimecode(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}
