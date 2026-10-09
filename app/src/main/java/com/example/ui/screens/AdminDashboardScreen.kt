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
import com.example.data.SampleData
import com.example.model.RecapVideo
import com.example.model.UserRole
import com.example.ui.AniRecapViewModel
import com.example.ui.components.formatViews
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AniRecapViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val allVideos by viewModel.allVideos.collectAsState()
    val pendingVideos by viewModel.pendingVideos.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Pending Queue, 1: All Videos, 2: Users

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Admin Header
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = CrimsonRedBright,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "ផ្ទាំងគ្រប់គ្រង Admin Dashboard",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "ពិនិត្យអនុម័តវីដេអូ, គ្រប់គ្រងអ្នកប្រើប្រាស់ និងត្រួតពិនិត្យប្រព័ន្ធ",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Metrics Overview Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard(
                    label = "Pending Queue",
                    count = "${pendingVideos.size}",
                    color = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    label = "Approved",
                    count = "${allVideos.count { it.isApproved }}",
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    label = "Total Views",
                    count = "635K",
                    color = AuraCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tab Selector Row (Pending, All Videos, Users)
        item {
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = DarkSurface,
                contentColor = CrimsonRedBright
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = {
                        Text(
                            "រង់ចាំការអនុម័ត (${pendingVideos.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedAdminTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedAdminTab == 0) WarningAmber else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_pending")
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = {
                        Text(
                            "កាតាឡុកវីដេអូ (${allVideos.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedAdminTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedAdminTab == 1) CrimsonRedBright else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_videos")
                )
                Tab(
                    selected = selectedAdminTab == 2,
                    onClick = { selectedAdminTab = 2 },
                    text = {
                        Text(
                            "អ្នកប្រើប្រាស់ (Users)",
                            fontSize = 12.sp,
                            fontWeight = if (selectedAdminTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedAdminTab == 2) ManaVioletLight else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_users")
                )
            }
        }

        // Tab 0: Pending Queue for Approval
        if (selectedAdminTab == 0) {
            if (pendingVideos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "គ្មានវីដេអូរង់ចាំការអនុម័តទេ! (All caught up)",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(pendingVideos, key = { it.id }) { pending ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pending_item_${pending.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = WarningAmber.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "PENDING APPROVAL",
                                        color = WarningAmber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "Submitted: ${pending.submittedDate}",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                            }

                            Text(
                                text = pending.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )

                            Text(
                                text = "Creator: ${pending.creatorName} • ${pending.type.displayName}",
                                fontSize = 12.sp,
                                color = ManaVioletLight
                            )

                            // Preview recap script summary
                            pending.khmerScript?.let { script ->
                                Surface(
                                    color = DarkSurfaceHighlight,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Hook: “${script.shortHook}”",
                                            fontSize = 11.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Rank Rule: ${script.rankSystemBreakdown}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            // Approval Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.adminApprove(pending.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_approve_btn_${pending.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("អនុម័ត (Approve & Publish)")
                                }

                                OutlinedButton(
                                    onClick = { viewModel.adminReject(pending.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                                    modifier = Modifier.testTag("admin_reject_btn_${pending.id}")
                                ) {
                                    Text("បដិសេធ (Reject)", color = ErrorRed)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tab 1: Video Catalog Management
        if (selectedAdminTab == 1) {
            items(allVideos, key = { it.id }) { video ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = video.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "${video.creatorName} • ${formatViews(video.viewsCount)} views • ${if (video.isApproved) "Live" else "Pending"}",
                                fontSize = 11.sp,
                                color = if (video.isApproved) SuccessGreen else WarningAmber
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { viewModel.openVideo(video.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "View",
                                    tint = AuraCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.adminDelete(video.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tab 2: Users & Roles Management
        if (selectedAdminTab == 2) {
            items(SampleData.mockUsers) { user ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )
                            Column {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = user.email,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        val badgeColor = Color(user.role.badgeColorHex)
                        Surface(
                            color = badgeColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = user.role.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = count,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}
