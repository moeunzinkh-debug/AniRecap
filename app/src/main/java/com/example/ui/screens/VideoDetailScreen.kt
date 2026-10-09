package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.Comment
import com.example.model.RecapVideo
import com.example.ui.AniRecapViewModel
import com.example.ui.components.KhmerScriptView
import com.example.ui.components.VideoPlayerView
import com.example.ui.components.formatViews
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    video: RecapVideo,
    viewModel: AniRecapViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val likedVideoIds by viewModel.likedVideoIds.collectAsState()
    val dislikedVideoIds by viewModel.dislikedVideoIds.collectAsState()
    val watchLaterIds by viewModel.watchLaterIds.collectAsState()
    val subscribedCreatorIds by viewModel.subscribedCreatorIds.collectAsState()
    val allComments by viewModel.comments.collectAsState()

    val isLiked = likedVideoIds.contains(video.id)
    val isDisliked = dislikedVideoIds.contains(video.id)
    val isWatchLater = watchLaterIds.contains(video.id)
    val isSubscribed = subscribedCreatorIds.contains(video.creatorId)

    val videoComments = remember(allComments, video.id) {
        allComments.filter { it.videoId == video.id }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("សម្រាយសាច់រឿង (Script)", "ឈុតឆាក (Chapters)", "ចំណាត់ថ្នាក់ & បក្ស (Ranks)", "មតិ (${videoComments.size})")

    var newCommentText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgMain)
    ) {
        // Video Player Component
        VideoPlayerView(
            video = video,
            onProgressUpdate = { seconds ->
                // Progress updated
            }
        )

        // Content Scrollable Column
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header Info: Title, Original Name & Stats
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = video.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary,
                        lineHeight = 24.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${video.originalTitle} • ${video.seasonEpisode}",
                            fontSize = 13.sp,
                            color = ManaVioletLight,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${formatViews(video.viewsCount)} views • ${video.year}",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }

                    // Interactive Action Buttons Bar (Like, Dislike, Watch Later, Share)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like
                        ActionPill(
                            icon = if (isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                            label = "${video.likesCount}",
                            isActive = isLiked,
                            activeColor = CrimsonRedBright,
                            onClick = { viewModel.toggleLike(video.id) },
                            testTag = "detail_like_button"
                        )

                        // Dislike
                        ActionPill(
                            icon = if (isDisliked) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                            label = "Dislike",
                            isActive = isDisliked,
                            activeColor = ErrorRed,
                            onClick = { viewModel.toggleDislike(video.id) },
                            testTag = "detail_dislike_button"
                        )

                        // Watch Later
                        ActionPill(
                            icon = if (isWatchLater) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            label = if (isWatchLater) "Saved" else "Watch Later",
                            isActive = isWatchLater,
                            activeColor = GoldLegendary,
                            onClick = { viewModel.toggleWatchLater(video.id) },
                            testTag = "detail_watch_later_button"
                        )

                        // Share
                        ActionPill(
                            icon = Icons.Default.Share,
                            label = "Share",
                            isActive = false,
                            activeColor = AuraCyan,
                            onClick = { /* Share link */ },
                            testTag = "detail_share_button"
                        )
                    }

                    Divider(
                        color = DarkSurfaceHighlight,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Creator Channel Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = video.creatorAvatar,
                                contentDescription = video.creatorName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Column {
                                Text(
                                    text = video.creatorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "42.8K subscribers",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleSubscribe(video.creatorId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSubscribed) DarkSurfaceHighlight else CrimsonRedBright
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("subscribe_button")
                        ) {
                            Text(
                                text = if (isSubscribed) "Subscribed ✓" else "Subscribe",
                                color = if (isSubscribed) TextSecondary else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Synopsis Card
                    Surface(
                        color = DarkSurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "សង្ខេបសាច់រឿង (Synopsis):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AuraCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = video.synopsis,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            // Tab Navigation Row
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurface,
                    contentColor = CrimsonRedBright,
                    edgePadding = 16.dp
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) CrimsonRedBright else TextSecondary,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier.testTag("detail_tab_$index")
                        )
                    }
                }
            }

            // Tab 0: Full Khmer Script (Plot-First + Rank/Faction Tracking)
            if (selectedTab == 0) {
                item {
                    KhmerScriptView(
                        script = video.khmerScript,
                        ranks = video.ranks,
                        factions = video.factions
                    )
                }
            }

            // Tab 1: Video Chapters
            if (selectedTab == 1) {
                if (video.chapters.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("មិនទាន់មាន Chapters ឡើយ", color = TextSecondary)
                        }
                    }
                } else {
                    items(video.chapters) { chapter ->
                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    color = DarkSurfaceHighlight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = chapter.formattedTime,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AuraCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = chapter.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    if (chapter.description.isNotBlank()) {
                                        Text(
                                            text = chapter.description,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Ranks & Factions
            if (selectedTab == 2) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ចំណាត់ថ្នាក់អំណាច & ឋានានុក្រម (Rank & Hierarchy Rules)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        video.ranks.forEach { rank ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = rank.entityName,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLegendary,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "ប្រព័ន្ធ៖ ${rank.rankSystem} | កម្រិតបច្ចុប្បន្ន៖ ${rank.currentRank}",
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "អត្ថន័យ៖ ${rank.significance}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "បក្សសម្ព័ន្ធ & អង្គភាព (Factions & Guilds)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        video.factions.forEach { faction ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${faction.factionName} (${faction.factionType})",
                                        fontWeight = FontWeight.Bold,
                                        color = AuraCyan,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "កេរ្តិ៍ឈ្មោះ៖ ${faction.reputation}",
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "តួនាទីតួអង្គ៖ ${faction.characterRole}",
                                        fontSize = 12.sp,
                                        color = ManaVioletLight
                                    )
                                    Text(
                                        text = "សារៈសំខាន់៖ ${faction.storyImpact}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tab 3: Comments Section
            if (selectedTab == 3) {
                // Post comment input
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = { Text("សរសេរមតិយោបល់របស់អ្នក... (Write comment)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("comment_input_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonRedBright,
                                unfocusedBorderColor = DarkSurfaceHighlight,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (newCommentText.isNotBlank()) {
                                        viewModel.addComment(video.id, newCommentText)
                                        newCommentText = ""
                                    }
                                },
                                enabled = newCommentText.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRedBright),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("post_comment_button")
                            ) {
                                Text("បញ្ជូនមតិ (Post)")
                            }
                        }
                    }
                }

                items(videoComments, key = { it.id }) { comment ->
                    CommentItem(
                        comment = comment,
                        onLikeClick = { viewModel.likeComment(comment.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = if (isActive) activeColor.copy(alpha = 0.2f) else DarkSurface,
        shape = RoundedCornerShape(20.dp),
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, activeColor) else null,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) activeColor else TextSecondary
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onLikeClick: () -> Unit
) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("comment_item_${comment.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                model = comment.userAvatar,
                contentDescription = comment.userName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = comment.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "• ${comment.timestampFormatted}",
                        fontSize = 10.sp,
                        color = TextTertiary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.content,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (comment.isLikedByMe) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                            contentDescription = "Like Comment",
                            tint = if (comment.isLikedByMe) CrimsonRedBright else TextTertiary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "${comment.likesCount}",
                        fontSize = 10.sp,
                        color = if (comment.isLikedByMe) CrimsonRedBright else TextTertiary
                    )
                }
            }
        }
    }
}
