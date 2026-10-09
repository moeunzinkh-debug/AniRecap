package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.UserRole
import com.example.ui.AniRecapViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: AniRecapViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    val watchLaterIds by viewModel.watchLaterIds.collectAsState()
    val likedVideoIds by viewModel.likedVideoIds.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .padding(16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = currentUser.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = currentUser.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )

                    Text(
                        text = currentUser.email,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    // Role Chip
                    val roleColor = Color(currentUser.role.badgeColorHex)
                    Surface(
                        color = roleColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, roleColor)
                    ) {
                        Text(
                            text = "Role: ${currentUser.role.label}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = roleColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    if (currentUser.bio.isNotBlank()) {
                        Text(
                            text = currentUser.bio,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(label = "Watched", value = "${watchHistory.size}", modifier = Modifier.weight(1f))
                ProfileStatCard(label = "Liked", value = "${likedVideoIds.size}", modifier = Modifier.weight(1f))
                ProfileStatCard(label = "Saved", value = "${watchLaterIds.size}", modifier = Modifier.weight(1f))
            }
        }

        // Role Switcher Section
        item {
            Text(
                text = "ប្តូរតួនាទីគណនី (Switch Role)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                UserRole.values().forEach { role ->
                    val isCurrent = role == currentUser.role
                    val color = Color(role.badgeColorHex)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrent) color.copy(alpha = 0.15f) else DarkSurface,
                        border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, color) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.switchRole(role) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = role.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }
                            if (isCurrent) {
                                Text(
                                    text = "សកម្ម (Active)",
                                    fontSize = 11.sp,
                                    color = color,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Special Quick Access Cards based on role
        if (currentUser.role == UserRole.CREATOR || currentUser.role == UserRole.ADMIN) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.CreatorStudio) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VideoCall, contentDescription = null, tint = ManaVioletLight)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("ចូលទៅកាន់ Creator Studio", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
        }

        if (currentUser.role == UserRole.ADMIN) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.AdminDashboard) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CrimsonRedBright)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("ចូលទៅកាន់ Admin Dashboard", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
