package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object AniRecapRepository {

    // Current logged-in user
    private val _currentUser = MutableStateFlow(SampleData.mockUsers[0]) // Start as VIEWER
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Video catalog
    private val _videos = MutableStateFlow<List<RecapVideo>>(SampleData.sampleRecaps)
    val videos: StateFlow<List<RecapVideo>> = _videos.asStateFlow()

    // User interactions
    private val _likedVideoIds = MutableStateFlow<Set<String>>(setOf("recap_solo_leveling"))
    val likedVideoIds: StateFlow<Set<String>> = _likedVideoIds.asStateFlow()

    private val _dislikedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    val dislikedVideoIds: StateFlow<Set<String>> = _dislikedVideoIds.asStateFlow()

    private val _watchLaterIds = MutableStateFlow<Set<String>>(setOf("recap_frieren"))
    val watchLaterIds: StateFlow<Set<String>> = _watchLaterIds.asStateFlow()

    private val _subscribedCreatorIds = MutableStateFlow<Set<String>>(setOf("user_creator"))
    val subscribedCreatorIds: StateFlow<Set<String>> = _subscribedCreatorIds.asStateFlow()

    private val _watchHistory = MutableStateFlow<List<WatchHistoryItem>>(
        listOf(
            WatchHistoryItem("recap_solo_leveling", 820, "ថ្ងៃនេះ ម៉ោង 10:15"),
            WatchHistoryItem("recap_jjk_shibuya", 450, "ម្សិលមិញ ម៉ោង 21:30")
        )
    )
    val watchHistory: StateFlow<List<WatchHistoryItem>> = _watchHistory.asStateFlow()

    // Comments
    private val _comments = MutableStateFlow<List<Comment>>(SampleData.sampleComments.toList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    // Switch active user role (Viewer / Creator / Admin)
    fun switchRole(role: UserRole) {
        val matchingUser = SampleData.mockUsers.firstOrNull { it.role == role }
            ?: SampleData.mockUsers.first().copy(role = role)
        _currentUser.value = matchingUser
    }

    fun toggleLike(videoId: String) {
        val currentLikes = _likedVideoIds.value.toMutableSet()
        val currentDislikes = _dislikedVideoIds.value.toMutableSet()

        if (currentLikes.contains(videoId)) {
            currentLikes.remove(videoId)
        } else {
            currentLikes.add(videoId)
            currentDislikes.remove(videoId)
        }
        _likedVideoIds.value = currentLikes
        _dislikedVideoIds.value = currentDislikes

        // update like count on video
        _videos.value = _videos.value.map { video ->
            if (video.id == videoId) {
                val newCount = if (currentLikes.contains(videoId)) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
                video.copy(likesCount = newCount)
            } else video
        }
    }

    fun toggleDislike(videoId: String) {
        val currentDislikes = _dislikedVideoIds.value.toMutableSet()
        val currentLikes = _likedVideoIds.value.toMutableSet()

        if (currentDislikes.contains(videoId)) {
            currentDislikes.remove(videoId)
        } else {
            currentDislikes.add(videoId)
            currentLikes.remove(videoId)
        }
        _dislikedVideoIds.value = currentDislikes
        _likedVideoIds.value = currentLikes
    }

    fun toggleWatchLater(videoId: String) {
        val currentSet = _watchLaterIds.value.toMutableSet()
        if (currentSet.contains(videoId)) {
            currentSet.remove(videoId)
        } else {
            currentSet.add(videoId)
        }
        _watchLaterIds.value = currentSet
    }

    fun toggleSubscribe(creatorId: String) {
        val currentSet = _subscribedCreatorIds.value.toMutableSet()
        if (currentSet.contains(creatorId)) {
            currentSet.remove(creatorId)
        } else {
            currentSet.add(creatorId)
        }
        _subscribedCreatorIds.value = currentSet
    }

    fun updateWatchProgress(videoId: String, watchedSeconds: Int) {
        val currentList = _watchHistory.value.filter { it.videoId != videoId }.toMutableList()
        currentList.add(0, WatchHistoryItem(videoId, watchedSeconds, "ឥឡូវនេះ"))
        _watchHistory.value = currentList
    }

    fun clearWatchHistory() {
        _watchHistory.value = emptyList()
    }

    fun addComment(videoId: String, content: String) {
        if (content.isBlank()) return
        val user = _currentUser.value
        val newComment = Comment(
            id = "c_${UUID.randomUUID()}",
            videoId = videoId,
            userId = user.id,
            userName = user.name,
            userAvatar = user.avatarUrl,
            role = user.role,
            content = content.trim(),
            timestampFormatted = "ឥឡូវនេះ",
            likesCount = 0
        )
        _comments.value = listOf(newComment) + _comments.value
    }

    fun likeComment(commentId: String) {
        _comments.value = _comments.value.map {
            if (it.id == commentId) {
                val newLiked = !it.isLikedByMe
                val newCount = if (newLiked) it.likesCount + 1 else (it.likesCount - 1).coerceAtLeast(0)
                it.copy(isLikedByMe = newLiked, likesCount = newCount)
            } else it
        }
    }

    // Creator Actions
    fun submitNewRecap(newVideo: RecapVideo) {
        _videos.value = listOf(newVideo) + _videos.value
    }

    // Admin Actions
    fun adminApproveRecap(videoId: String) {
        _videos.value = _videos.value.map {
            if (it.id == videoId) {
                it.copy(isApproved = true, approvalNotes = "អនុម័តដោយ Admin រួចរាល់")
            } else it
        }
    }

    fun adminRejectRecap(videoId: String, reason: String) {
        _videos.value = _videos.value.map {
            if (it.id == videoId) {
                it.copy(isApproved = false, approvalNotes = reason)
            } else it
        }
    }

    fun adminDeleteRecap(videoId: String) {
        _videos.value = _videos.value.filter { it.id != videoId }
    }

    fun adminToggleFeatured(videoId: String) {
        _videos.value = _videos.value.map {
            if (it.id == videoId) it.copy(isFeatured = !it.isFeatured) else it
        }
    }
}
