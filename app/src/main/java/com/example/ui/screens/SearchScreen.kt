package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoType
import com.example.ui.AniRecapViewModel
import com.example.ui.components.VideoCard
import com.example.ui.theme.*

@Composable
fun SearchScreen(
    viewModel: AniRecapViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val videos by viewModel.filteredVideos.collectAsState()
    val watchLaterIds by viewModel.watchLaterIds.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("ស្វែងរក Anime, Movie, តួអង្គ, Creator...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedBorderColor = CrimsonRedBright,
                unfocusedBorderColor = DarkSurfaceHighlight
            )
        )

        // Type Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedType == null,
                onClick = { viewModel.selectedType.value = null },
                label = { Text("ទាំងអស់") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CrimsonRedBright,
                    selectedLabelColor = Color.White
                )
            )

            VideoType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { viewModel.selectedType.value = if (selectedType == type) null else type },
                    label = { Text(type.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CrimsonRedBright,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Text(
            text = "លទ្ធផលស្វែងរក (${videos.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )

        // Results List
        if (videos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "មិនមានលទ្ធផលសម្រាប់ \"$searchQuery\"",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(videos, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        isWatchLater = watchLaterIds.contains(video.id),
                        onVideoClick = { viewModel.openVideo(video.id) },
                        onWatchLaterToggle = { viewModel.toggleWatchLater(video.id) }
                    )
                }
            }
        }
    }
}
