package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.AniRecapViewModel
import com.example.ui.components.VideoCard
import com.example.ui.theme.*

@Composable
fun WatchHistoryScreen(
    viewModel: AniRecapViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var activeTab by remember { mutableIntStateOf(0) } // 0: History, 1: Watch Later
    val watchHistory by viewModel.watchHistory.collectAsState()
    val watchLaterIds by viewModel.watchLaterIds.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()

    val historyVideos = remember(watchHistory, allVideos) {
        watchHistory.mapNotNull { item ->
            val v = allVideos.firstOrNull { it.id == item.videoId }
            if (v != null) Pair(v, item) else null
        }
    }

    val watchLaterVideos = remember(watchLaterIds, allVideos) {
        allVideos.filter { watchLaterIds.contains(it.id) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .testTag("watch_history_screen")
    ) {
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkSurface,
            contentColor = CrimsonRedBright
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        "ប្រវត្តិទស្សនា (${historyVideos.size})",
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (activeTab == 0) CrimsonRedBright else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_history")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        "Watch Later (${watchLaterVideos.size})",
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (activeTab == 1) GoldLegendary else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_watch_later")
            )
        }

        if (activeTab == 0) {
            // History Tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "វីដេអូដែលបានទស្សនាកន្លងមក",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                if (historyVideos.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearHistory() }) {
                        Text("សម្អាតប្រវត្តិ (Clear)", color = ErrorRed, fontSize = 12.sp)
                    }
                }
            }

            if (historyVideos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("មិនទាន់មានប្រវត្តិទស្សនាទេ", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(historyVideos) { (video, item) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openVideo(video.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 120.dp, height = 75.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = video.thumbnailUrl,
                                        contentDescription = video.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = video.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        maxLines = 2
                                    )
                                    Text(
                                        text = video.creatorName,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "ទស្សនា: ${item.lastWatchedDate}",
                                        fontSize = 10.sp,
                                        color = AuraCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Watch Later Tab
            if (watchLaterVideos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("មិនទាន់បានរក្សាទុកវីដេអូសម្រាប់មើលពេលក្រោយទេ", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(watchLaterVideos, key = { it.id }) { video ->
                        VideoCard(
                            video = video,
                            isWatchLater = true,
                            onVideoClick = { viewModel.openVideo(video.id) },
                            onWatchLaterToggle = { viewModel.toggleWatchLater(video.id) }
                        )
                    }
                }
            }
        }
    }
}
