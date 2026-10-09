package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoType
import com.example.ui.AniRecapViewModel
import com.example.ui.components.VideoCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorStudioScreen(
    viewModel: AniRecapViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()
    val myVideos = remember(allVideos, currentUser.id) {
        allVideos.filter { it.creatorId == currentUser.id }
    }

    var showUploadDialog by remember { mutableStateOf(false) }

    // Upload Form States
    var title by remember { mutableStateOf("") }
    var originalTitle by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(VideoType.ANIME) }
    var seasonEpisode by remember { mutableStateOf("Season 1") }
    var releaseYear by remember { mutableStateOf("2024") }
    var genreText by remember { mutableStateOf("Action, Fantasy, Supernatural") }
    var durationMinutes by remember { mutableStateOf("18") }
    var thumbnailUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600") }
    var synopsis by remember { mutableStateOf("") }

    // Khmer Script Master Prompt States
    var hookText by remember { mutableStateOf("ក្មេងប្រុសម្នាក់ដែលគ្រប់គ្នាមើលងាយ បែរជាបើកបានសមត្ថភាពសម្ងាត់ដែលអាចផ្លាស់ប្តូរជីវិតរបស់គាត់ទាំងមូល!") }
    var introPhrase by remember { mutableStateOf("នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា...") }
    var worldExplanation by remember { mutableStateOf("ពិភពលោកត្រូវបានគ្រប់គ្រងដោយប្រព័ន្ធ Hunter និង Dungeon ចាប់ពី Rank E ដល់ Rank S។") }
    var characterIntro by remember { mutableStateOf("តួអង្គសំខាន់រស់នៅក្នុងភាពខ្វះខាត និងប្រឈមនឹងគ្រោះថ្នាក់ដើម្បីគ្រួសារ។") }
    var mainStory by remember { mutableStateOf("បន្ទាប់ពីជួបឧបសគ្គធំ តួអង្គបានទទួលសមត្ថភាពសម្ងាត់ដែលអនុញ្ញាតឲ្យគាត់វិវត្តន៍ខ្លួនលឿនជាងធម្មតា។") }
    var rankBreakdown by remember { mutableStateOf("តួអង្គមាន Personal Rank ខ្សោយ ប៉ុន្តែមាន Hidden Combat Power ខ្ពស់លើសពីកម្រិត Guild ធម្មតា។") }
    var factionDynamics by remember { mutableStateOf("Guild លំដាប់កំពូលព្យាយាមទាក់ទង និងអញ្ជើញគាត់ចូលរួម ប៉ុន្តែគាត់ជ្រើសរើសផ្លូវឯករាជ្យ។") }
    var climaxBattle by remember { mutableStateOf("ការប្រយុទ្ធស្វិតស្វាញជាមួយ Dungeon Boss ដោយប្រើប្រាស់ភាពឆ្លាតវៃ និងចំណុចខ្សោយរបស់សត្រូវ។") }
    var endingDirection by remember { mutableStateOf("តួអង្គទទួលបានជ័យជម្នះ ដោះសោរ Skill ថ្មី និងបើកផ្លូវទៅកាន់ដំណើរផ្សងព្រេងបន្ទាប់។") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .testTag("creator_studio_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Creator Studio (ស្ទូឌីយោបង្កើត)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "ឆានែល៖ ${currentUser.channelName ?: currentUser.name}",
                        fontSize = 13.sp,
                        color = ManaVioletLight
                    )
                }

                Button(
                    onClick = { showUploadDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ManaViolet),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("open_upload_modal_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload Recap")
                }
            }
        }

        // Stats Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StudioStatCard(
                    title = "Total Views",
                    value = "435.5K",
                    icon = Icons.Default.Visibility,
                    tint = AuraCyan,
                    modifier = Modifier.weight(1f)
                )
                StudioStatCard(
                    title = "Subscribers",
                    value = "42.8K",
                    icon = Icons.Default.People,
                    tint = ManaVioletLight,
                    modifier = Modifier.weight(1f)
                )
                StudioStatCard(
                    title = "My Recaps",
                    value = "${myVideos.size}",
                    icon = Icons.Default.VideoLibrary,
                    tint = CrimsonRedBright,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Upload Prompt Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "បង្កើត Recap តាមទម្រង់ Master Prompt Khmer Anime Recap",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "រចនាសម្ព័ន្ធ Plot-First (សាច់រឿង 80% + 20% Recap Commentary) ព្រមទាំងការកត់ត្រា Rank, Level, Faction មិនច្រឡំគ្នា។",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Button(
                        onClick = { showUploadDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRedBright),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("បង្កើត និងដាក់ស្នើថ្មី (New Submission)")
                    }
                }
            }
        }

        // Submissions List Header
        item {
            Text(
                text = "វីដេអូរបស់ខ្ញុំ & ស្ថានភាពត្រួតពិនិត្យ (${myVideos.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }

        // Submissions
        if (myVideos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("អ្នកមិនទាន់បាន Upload វីដេអូណាមួយនៅឡើយទេ", color = TextSecondary)
                }
            }
        } else {
            items(myVideos, key = { it.id }) { video ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = video.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Status Badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (video.isApproved) SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (video.isApproved) SuccessGreen else WarningAmber)
                            ) {
                                Text(
                                    text = if (video.isApproved) "Approved ✓" else "Pending Review ⏳",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (video.isApproved) SuccessGreen else WarningAmber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${video.type.displayName} • ${video.formattedDuration} • ${video.viewsCount} views",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        if (!video.isApproved) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Admin Note: ${video.approvalNotes}",
                                fontSize = 12.sp,
                                color = WarningAmber
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog for Upload & Khmer Script Writing
    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = {
                Text(
                    text = "Upload Recap & សរសេរ Script តាមក្បួនខ្នាត",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("ចំណងជើង Recap (Title)") },
                            placeholder = { Text("សម្រាយរឿង Solo Leveling...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_title_field")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = originalTitle,
                            onValueChange = { originalTitle = it },
                            label = { Text("ឈ្មោះរឿងដើម (Original Anime/Movie Name)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = seasonEpisode,
                                onValueChange = { seasonEpisode = it },
                                label = { Text("Season / Episode") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = releaseYear,
                                onValueChange = { releaseYear = it },
                                label = { Text("Year") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = genreText,
                            onValueChange = { genreText = it },
                            label = { Text("Genres (បំបែកដោយក្បៀស)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it },
                            label = { Text("រយៈពេល (នាទី)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = synopsis,
                            onValueChange = { synopsis = it },
                            label = { Text("សង្ខេបសាច់រឿងសរុប (Synopsis)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    // Khmer Recap Master Script Section
                    item {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "MASTER PROMPT: KHMER ANIME RECAP NARRATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ManaVioletLight
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = hookText,
                            onValueChange = { hookText = it },
                            label = { Text("A. Hook (ចំណុចទាក់ទាញដំបូង)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = introPhrase,
                            onValueChange = { introPhrase = it },
                            label = { Text("B. Opening Phrase") },
                            placeholder = { Text("នៅដើមសាច់រឿង គេបានបង្ហាញឲ្យឃើញថា...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = worldExplanation,
                            onValueChange = { worldExplanation = it },
                            label = { Text("C. World / System Explanation") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = mainStory,
                            onValueChange = { mainStory = it },
                            label = { Text("E. Progression (ដំណើររឿង 80% Story + 20% Recap)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = rankBreakdown,
                            onValueChange = { rankBreakdown = it },
                            label = { Text("D. Rank & Status (កុំច្រឡំ Personal Rank ជាមួយ Guild)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = climaxBattle,
                            onValueChange = { climaxBattle = it },
                            label = { Text("G. Climax Battle (យុទ្ធសាស្ត្រប្រយុទ្ធ)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = endingDirection,
                            onValueChange = { endingDirection = it },
                            label = { Text("H. Result & New Direction") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.createRecapSubmission(
                                title = title,
                                originalTitle = if (originalTitle.isNotBlank()) originalTitle else title,
                                type = selectedType,
                                seasonEpisode = seasonEpisode,
                                year = releaseYear.toIntOrNull() ?: 2024,
                                genres = genreText.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                durationMinutes = durationMinutes.toIntOrNull() ?: 15,
                                thumbnailUrl = thumbnailUrl,
                                synopsis = if (synopsis.isNotBlank()) synopsis else "សង្ខេបសាច់រឿង anime recap ដោយ ${currentUser.name}",
                                hook = hookText,
                                introPhrase = introPhrase,
                                worldExplanation = worldExplanation,
                                characterIntro = characterIntro,
                                mainStory = mainStory,
                                rankBreakdown = rankBreakdown,
                                factionDynamics = factionDynamics,
                                climaxBattle = climaxBattle,
                                endingDirection = endingDirection
                            )
                            showUploadDialog = false
                            title = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRedBright),
                    modifier = Modifier.testTag("submit_upload_btn")
                ) {
                    Text("ដាក់ស្នើ (Submit to Admin)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text("បោះបង់ (Cancel)", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun StudioStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
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
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text(text = title, fontSize = 10.sp, color = TextSecondary)
        }
    }
}
