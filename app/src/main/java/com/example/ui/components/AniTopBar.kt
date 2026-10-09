package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AniTopBar(
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    onSearchClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    var showRoleDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onNavigate(Screen.Home) }
                    .testTag("app_logo_home")
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CrimsonRedBright, ManaViolet)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "AniRecap Play",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row {
                        Text(
                            text = "Ani",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = CrimsonRedBright
                        )
                        Text(
                            text = "Recap",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = AuraCyan
                        )
                    }
                    Text(
                        text = "សម្រាយសាច់រឿង",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Actions & Role Switcher
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Role Badge Chip
                val badgeColor = Color(currentRole.badgeColorHex)
                Surface(
                    color = badgeColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clickable { showRoleDialog = true }
                        .testTag("role_switcher_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentRole.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Switch Role",
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Search Icon
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("topbar_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Recaps",
                        tint = TextPrimary
                    )
                }

                // Watch History Icon
                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("topbar_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Watch History",
                        tint = TextPrimary
                    )
                }
            }
        }
    }

    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = {
                Text(
                    text = "ជ្រើសរើសតួនាទី (Switch Role)",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "AniRecap គាំទ្រ 3 Roles ពិតប្រាកដសម្រាប់ការប្រើប្រាស់:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    UserRole.values().forEach { role ->
                        val isSelected = role == currentRole
                        val color = Color(role.badgeColorHex)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) color.copy(alpha = 0.2f) else DarkSurfaceHighlight,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRoleChange(role)
                                    showRoleDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = role.label,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) color else TextPrimary,
                                        fontSize = 15.sp
                                    )
                                    val desc = when (role) {
                                        UserRole.VIEWER -> "ទស្សនា, ចូលចិត្ត, Comment, Watch Later & History"
                                        UserRole.CREATOR -> "បង្ហោះវីដេអូ Recap & សរសេរ Script តាមក្បួនខ្នាត"
                                        UserRole.ADMIN -> "ពិនិត្យ និងអនុម័តវីដេអូ (Approve/Reject), គ្រប់គ្រង Users"
                                    }
                                    Text(
                                        text = desc,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = false }) {
                    Text("បិទ (Close)", color = AuraCyan)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}
