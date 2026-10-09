package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AniRecapRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class Screen {
    data object Home : Screen()
    data class VideoDetail(val videoId: String) : Screen()
    data object CreatorStudio : Screen()
    data object AdminDashboard : Screen()
    data object WatchHistory : Screen()
    data object Search : Screen()
    data object Profile : Screen()
}

data class FilterState(
    val query: String = "",
    val type: VideoType? = null,
    val genre: String = "All",
    val year: Int? = null,
    val sortBy: String = "Trending"
)

class AniRecapViewModel : ViewModel() {

    private val repository = AniRecapRepository

    val currentUser = repository.currentUser
    val allVideos = repository.videos
    val likedVideoIds = repository.likedVideoIds
    val dislikedVideoIds = repository.dislikedVideoIds
    val watchLaterIds = repository.watchLaterIds
    val subscribedCreatorIds = repository.subscribedCreatorIds
    val watchHistory = repository.watchHistory
    val comments = repository.comments

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen back stack
    private val backStack = mutableListOf<Screen>()

    // Filter states
    val searchQuery = MutableStateFlow("")
    val selectedType = MutableStateFlow<VideoType?>(null)
    val selectedGenre = MutableStateFlow("All")
    val selectedYear = MutableStateFlow<Int?>(null)
    val sortBy = MutableStateFlow("Trending") // "Trending", "Newest", "Duration"

    // Combine filter params into one state
    val filterState: StateFlow<FilterState> = combine(
        searchQuery,
        selectedType,
        selectedGenre,
        selectedYear
    ) { query, type, genre, year ->
        Pair(Pair(query, type), Pair(genre, year))
    }.combine(sortBy) { (qt, gy), sort ->
        FilterState(
            query = qt.first,
            type = qt.second,
            genre = gy.first,
            year = gy.second,
            sortBy = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FilterState())

    // Active playing video state
    private val _currentVideoId = MutableStateFlow<String?>(null)
    val currentVideoId: StateFlow<String?> = _currentVideoId.asStateFlow()

    val currentVideo: StateFlow<RecapVideo?> = combine(allVideos, _currentVideoId) { videos, id ->
        videos.firstOrNull { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered approved videos for home/search
    val filteredVideos: StateFlow<List<RecapVideo>> = combine(
        allVideos,
        filterState
    ) { videos: List<RecapVideo>, filter: FilterState ->
        videos
            .filter { it.isApproved }
            .filter { video ->
                if (filter.query.isBlank()) true
                else {
                    video.title.contains(filter.query, ignoreCase = true) ||
                    video.originalTitle.contains(filter.query, ignoreCase = true) ||
                    video.creatorName.contains(filter.query, ignoreCase = true) ||
                    video.genres.any { it.contains(filter.query, ignoreCase = true) }
                }
            }
            .filter { filter.type == null || it.type == filter.type }
            .filter { filter.genre == "All" || it.genres.any { g -> g.equals(filter.genre, ignoreCase = true) } }
            .filter { filter.year == null || it.year == filter.year }
            .let { list ->
                when (filter.sortBy) {
                    "Trending" -> list.sortedByDescending { it.viewsCount }
                    "Newest" -> list.sortedByDescending { it.id }
                    "Duration" -> list.sortedByDescending { it.durationSeconds }
                    else -> list
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending videos for Admin
    val pendingVideos: StateFlow<List<RecapVideo>> = allVideos.map { list ->
        list.filter { !it.isApproved }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun openVideo(videoId: String) {
        _currentVideoId.value = videoId
        navigateTo(Screen.VideoDetail(videoId))
        // track in history
        repository.updateWatchProgress(videoId, 0)
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            val previous = backStack.removeAt(backStack.size - 1)
            _currentScreen.value = previous
            return true
        }
        if (_currentScreen.value !is Screen.Home) {
            _currentScreen.value = Screen.Home
            return true
        }
        return false
    }

    fun switchRole(role: UserRole) {
        repository.switchRole(role)
    }

    fun toggleLike(videoId: String) {
        repository.toggleLike(videoId)
    }

    fun toggleDislike(videoId: String) {
        repository.toggleDislike(videoId)
    }

    fun toggleWatchLater(videoId: String) {
        repository.toggleWatchLater(videoId)
    }

    fun toggleSubscribe(creatorId: String) {
        repository.toggleSubscribe(creatorId)
    }

    fun addComment(videoId: String, content: String) {
        repository.addComment(videoId, content)
    }

    fun likeComment(commentId: String) {
        repository.likeComment(commentId)
    }

    fun clearHistory() {
        repository.clearWatchHistory()
    }

    // Admin commands
    fun adminApprove(videoId: String) {
        repository.adminApproveRecap(videoId)
    }

    fun adminReject(videoId: String, reason: String = "ត្រូវការកែសម្រួលបន្ថែមលើចំណាត់ថ្នាក់ Rank") {
        repository.adminRejectRecap(videoId, reason)
    }

    fun adminDelete(videoId: String) {
        repository.adminDeleteRecap(videoId)
    }

    // Creator submission
    fun createRecapSubmission(
        title: String,
        originalTitle: String,
        type: VideoType,
        seasonEpisode: String,
        year: Int,
        genres: List<String>,
        durationMinutes: Int,
        thumbnailUrl: String,
        synopsis: String,
        hook: String,
        introPhrase: String,
        worldExplanation: String,
        characterIntro: String,
        mainStory: String,
        rankBreakdown: String,
        factionDynamics: String,
        climaxBattle: String,
        endingDirection: String
    ) {
        val user = currentUser.value
        val newVideo = RecapVideo(
            id = "recap_${UUID.randomUUID().toString().take(8)}",
            title = title,
            originalTitle = originalTitle,
            type = type,
            seasonEpisode = seasonEpisode,
            year = year,
            genres = genres,
            durationSeconds = durationMinutes * 60,
            thumbnailUrl = if (thumbnailUrl.isNotBlank()) thumbnailUrl else "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600",
            creatorId = user.id,
            creatorName = user.channelName ?: user.name,
            creatorAvatar = user.avatarUrl,
            viewsCount = 0,
            likesCount = 0,
            dislikesCount = 0,
            isApproved = false, // Must be approved by Admin
            approvalNotes = "កំពុងរង់ចាំការត្រួតពិនិត្យពី Admin",
            synopsis = synopsis,
            chapters = listOf(
                VideoChapter(0, "Hook & ការណែនាំ", "សេចក្តីផ្តើមនៃសាច់រឿង"),
                VideoChapter(180, "ពិភពលោក និងប្រព័ន្ធអំណាច", "ការពន្យល់ Rank & Hierarchy"),
                VideoChapter(540, "ជម្លោះ និងការប្រយុទ្ធធំ", "Climax Battle"),
                VideoChapter(durationMinutes * 50, "លទ្ធផល និងទិសដៅថ្មី", "Ending")
            ),
            ranks = listOf(
                RankHierarchy(
                    entityName = "តួអង្គចម្បង",
                    category = "Individual Combat Power",
                    rankSystem = "Rank F - Rank S",
                    currentRank = "Hidden / Progressive Rank",
                    significance = rankBreakdown
                )
            ),
            factions = listOf(
                FactionStatus(
                    factionName = "អង្គភាព / ក្រុមចម្បង",
                    factionType = "Guild / Alliance",
                    reputation = "Famous / Top Tier",
                    characterRole = "Key Fighter",
                    storyImpact = factionDynamics
                )
            ),
            khmerScript = KhmerScript(
                recapTitle = title,
                shortHook = hook,
                introPhrase = introPhrase,
                worldExplanation = worldExplanation,
                characterIntro = characterIntro,
                mainStoryRecap = mainStory,
                rankSystemBreakdown = rankBreakdown,
                factionDynamics = factionDynamics,
                majorBattleClimax = climaxBattle,
                endingDirection = endingDirection,
                visualActionNotes = "Visual Recap 80% Story + 20% Commentary"
            )
        )

        repository.submitNewRecap(newVideo)
    }
}
