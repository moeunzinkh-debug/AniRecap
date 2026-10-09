package com.example.model

enum class VideoType(val displayName: String) {
    ANIME("Anime Recap"),
    MOVIE("Movie Recap"),
    MANHWA("Manhwa / Series")
}

data class VideoChapter(
    val timestampSeconds: Int,
    val title: String,
    val description: String = ""
) {
    val formattedTime: String
        get() {
            val minutes = timestampSeconds / 60
            val seconds = timestampSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}

data class RankHierarchy(
    val entityName: String,
    val category: String, // e.g., "Individual Hunter Rank", "Monster Threat Level", "Weapon Grade"
    val rankSystem: String, // e.g., "S / A / B / C / D / E"
    val currentRank: String, // e.g., "Rank E -> Rank S (Shadow Monarch)"
    val significance: String // Impact on plot & battles
)

data class FactionStatus(
    val factionName: String, // e.g., "Hunters Guild", "White Tiger", "Shadow Army"
    val factionType: String, // Guild, Clan, Unit, Enemy Faction
    val reputation: String, // Elite, National Level, Feared
    val characterRole: String, // Solo Member, Supreme Monarch
    val storyImpact: String
)

data class KhmerScript(
    val recapTitle: String,
    val shortHook: String,
    val introPhrase: String,
    val worldExplanation: String,
    val characterIntro: String,
    val mainStoryRecap: String,
    val rankSystemBreakdown: String,
    val factionDynamics: String,
    val majorBattleClimax: String,
    val endingDirection: String,
    val visualActionNotes: String = ""
)

data class RecapVideo(
    val id: String,
    val title: String,
    val originalTitle: String,
    val type: VideoType,
    val seasonEpisode: String,
    val year: Int,
    val genres: List<String>,
    val durationSeconds: Int,
    val thumbnailUrl: String,
    val videoPreviewUrl: String = "",
    val creatorId: String,
    val creatorName: String,
    val creatorAvatar: String,
    val viewsCount: Long,
    val likesCount: Long,
    val dislikesCount: Long = 0,
    val isApproved: Boolean = true,
    val approvalNotes: String = "",
    val submittedDate: String = "2024",
    val synopsis: String,
    val chapters: List<VideoChapter> = emptyList(),
    val ranks: List<RankHierarchy> = emptyList(),
    val factions: List<FactionStatus> = emptyList(),
    val khmerScript: KhmerScript? = null,
    val isFeatured: Boolean = false
) {
    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}
