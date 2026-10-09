package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.RecapVideo
import com.example.model.UserRole
import com.example.model.VideoType
import com.example.ui.AniRecapViewModel
import com.example.ui.Screen
import com.example.ui.components.VideoCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AniRecapViewModel,
    modifier: Modifier = Modifier
) {
    val videos by viewModel.filteredVideos.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()
    val watchLaterIds by viewModel.watchLaterIds.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    val featuredVideo = remember(allVideos) {
        allVideos.firstOrNull { it.isFeatured && it.isApproved } ?: allVideos.firstOrNull { it.isApproved }
    }

    val genres = listOf("All", "Action", "Fantasy", "Isekai / Dungeon", "Supernatural", "Dark Fantasy", "Drama", "Biography")
    val years = listOf(null, 2024, 2023, 2022)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Role Actions Banner (If Creator or Admin)
        if (currentUser.role == UserRole.CREATOR || currentUser.role == UserRole.ADMIN) {
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentUser.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.VideoCall,
                                contentDescription = null,
                                tint = if (currentUser.role == UserRole.ADMIN) CrimsonRedBright else ManaVioletLight
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (currentUser.role == UserRole.ADMIN) "Admin Controls Active" else "Creator Studio Active",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (currentUser.role == UserRole.ADMIN) "Review pending submissions & manage platform" else "Create and upload recap videos & scripts",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (currentUser.role == UserRole.ADMIN) {
                                    viewModel.navigateTo(Screen.AdminDashboard)
                                } else {
                                    viewModel.navigateTo(Screen.CreatorStudio)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentUser.role == UserRole.ADMIN) CrimsonRedBright else ManaViolet
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("quick_action_role_button")
                        ) {
                            Text(
                                text = if (currentUser.role == UserRole.ADMIN) "Admin Panel" else "Upload Video",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Hero Featured Banner
        featuredVideo?.let { featured ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(260.dp)
                        .clickable { viewModel.openVideo(featured.id) }
                        .testTag("featured_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = featured.thumbnailUrl,
                            contentDescription = featured.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Vignette and bottom gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Black.copy(alpha = 0.95f)
                                        )
                                    )
                                )
                        )

                        // Featured Tag
                        Surface(
                            color = CrimsonRedBright,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Text(
                                text = "★ FEATURED RECAP",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Content Details
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = featured.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "${featured.type.displayName} • ${featured.seasonEpisode} • ${featured.year}",
                                fontSize = 12.sp,
                                color = AuraCyan,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                modifier = Modifier.padding(top = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.openVideo(featured.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRedBright),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("featured_play_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ទស្សនាឥឡូវនេះ (Watch)")
                                }

                                OutlinedButton(
                                    onClick = { viewModel.toggleWatchLater(featured.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = if (watchLaterIds.contains(featured.id)) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = if (watchLaterIds.contains(featured.id)) GoldLegendary else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (watchLaterIds.contains(featured.id)) "បានរក្សាទុក" else "Watch Later",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Type Tabs (All, Anime, Movie, Manhwa)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Chip
                FilterChip(
                    selected = selectedType == null,
                    onClick = { viewModel.selectedType.value = null },
                    label = { Text("ទាំងអស់ (All)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CrimsonRedBright,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_all_type")
                )

                VideoType.values().forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { viewModel.selectedType.value = if (selectedType == type) null else type },
                        label = { Text(type.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CrimsonRedBright,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_type_${type.name}")
                    )
                }
            }
        }

        // Genre Horizontal Filter Row
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "ប្រភេទសាច់រឿង (Genres)",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres) { genre ->
                        val isSelected = selectedGenre == genre
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) ManaViolet else DarkSurfaceHighlight,
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectedGenre.value = genre
                                }
                                .testTag("genre_chip_$genre")
                        ) {
                            Text(
                                text = genre,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Sorting & Year Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Year selection
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ឆ្នាំ:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    years.forEach { year ->
                        val isSelected = selectedYear == year
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) AuraCyan.copy(alpha = 0.2f) else DarkSurfaceHighlight,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, AuraCyan) else null,
                            modifier = Modifier.clickable {
                                viewModel.selectedYear.value = year
                            }
                        ) {
                            Text(
                                text = year?.toString() ?: "All",
                                fontSize = 11.sp,
                                color = if (isSelected) AuraCyan else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Sort toggle (Trending / Newest / Duration)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceHighlight,
                    modifier = Modifier.clickable {
                        viewModel.sortBy.value = when (sortBy) {
                            "Trending" -> "Newest"
                            "Newest" -> "Duration"
                            else -> "Trending"
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sortBy,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Results Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "វីដេអូសម្រាយពេញនិយម (${videos.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Video Cards List
        if (videos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MovieFilter,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "មិនមានវីដេអូត្រូវនឹងលក្ខខណ្ឌស្វែងរកទេ",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(videos, key = { it.id }) { video ->
                VideoCard(
                    video = video,
                    isWatchLater = watchLaterIds.contains(video.id),
                    onVideoClick = { viewModel.openVideo(video.id) },
                    onWatchLaterToggle = { viewModel.toggleWatchLater(video.id) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
