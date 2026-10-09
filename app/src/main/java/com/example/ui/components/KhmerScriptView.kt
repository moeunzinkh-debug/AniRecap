package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KhmerScript
import com.example.model.RankHierarchy
import com.example.model.FactionStatus
import com.example.ui.theme.*

@Composable
fun KhmerScriptView(
    script: KhmerScript?,
    ranks: List<RankHierarchy>,
    factions: List<FactionStatus>,
    modifier: Modifier = Modifier
) {
    if (script == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "មិនទាន់មាន Script សម្រាយសាច់រឿងនៅឡើយទេ",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Format Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ManaViolet.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ManaViolet.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = ManaVioletLight,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "KHMER ANIME RECAP: PLOT-FIRST + RANK/FACTION TRACKING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ManaVioletLight
                    )
                    Text(
                        text = "សមាមាត្រ៖ សាច់រឿង 80% + ការអធិប្បាយ/សំនៀង Recap 20%",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Section A: Hook
        SectionCard(
            title = "A. Hook (ចំណុចទាក់ទាញដំបូង)",
            badgeColor = CrimsonRedBright,
            icon = Icons.Default.Bolt
        ) {
            Text(
                text = "“${script.shortHook}”",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 22.sp
            )
        }

        // Section B: Opening & World System
        SectionCard(
            title = "B. ការបើកសាច់រឿង & ប្រព័ន្ធពិភពលោក (Opening & World System)",
            badgeColor = AuraCyan,
            icon = Icons.Default.Public
        ) {
            Text(
                text = "ឃ្លាបើករឿង៖ “${script.introPhrase}”",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AuraCyan,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = script.worldExplanation,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }

        // Section C: Character Intro & Progression
        SectionCard(
            title = "C. ការណែនាំតួអង្គ & ដំណើររឿង (Progression & Incident)",
            badgeColor = GoldLegendary,
            icon = Icons.Default.Person
        ) {
            Text(
                text = script.characterIntro,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = script.mainStoryRecap,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 21.sp
            )
        }

        // Section D: Rank, Level, Status & Hierarchy Tracker
        if (ranks.isNotEmpty()) {
            SectionCard(
                title = "D. ការវិភាគ Rank & Level មិនច្រឡំគ្នា (Rank & Hierarchy)",
                badgeColor = CrimsonRedBright,
                icon = Icons.Default.MilitaryTech
            ) {
                ranks.forEach { rank ->
                    Surface(
                        color = DarkSurfaceHighlight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = rank.entityName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = rank.currentRank,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = GoldLegendary
                                )
                            }
                            Text(
                                text = "ប្រភេទ៖ ${rank.category} (${rank.rankSystem})",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ផលប៉ះពាល់៖ ${rank.significance}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Section E: Factions & Guilds
        if (factions.isNotEmpty()) {
            SectionCard(
                title = "E. ក្រុម & បក្សសម្ព័ន្ធ (Groups, Guilds & Factions)",
                badgeColor = ManaViolet,
                icon = Icons.Default.Shield
            ) {
                factions.forEach { faction ->
                    Surface(
                        color = DarkSurfaceHighlight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = faction.factionName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = faction.reputation,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = AuraCyan
                                )
                            }
                            Text(
                                text = "តួនាទីតួអង្គ៖ ${faction.characterRole}",
                                fontSize = 12.sp,
                                color = ManaVioletLight
                            )
                            Text(
                                text = "សារៈសំខាន់ក្នុងសាច់រឿង៖ ${faction.storyImpact}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Section F: Climax Battle & Strategy
        SectionCard(
            title = "F. សមរភូមិកំពូល & យុទ្ធសាស្ត្រប្រយុទ្ធ (Climax Battle)",
            badgeColor = CrimsonRedBright,
            icon = Icons.Default.FlashOn
        ) {
            Text(
                text = script.majorBattleClimax,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 21.sp
            )
        }

        // Section G: Ending & Next Direction
        SectionCard(
            title = "G. លទ្ធផល & ទិសដៅរឿងបន្ទាប់ (Result & Direction)",
            badgeColor = SuccessGreen,
            icon = Icons.Default.Explore
        ) {
            Text(
                text = script.endingDirection,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 21.sp
            )
            if (script.visualActionNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Visual Action Cue: ${script.visualActionNotes}",
                        fontSize = 11.sp,
                        color = TextTertiary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
