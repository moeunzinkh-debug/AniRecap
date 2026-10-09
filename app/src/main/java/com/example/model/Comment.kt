package com.example.model

data class Comment(
    val id: String,
    val videoId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val role: UserRole = UserRole.VIEWER,
    val content: String,
    val timestampFormatted: String,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false
)

data class WatchHistoryItem(
    val videoId: String,
    val watchedSeconds: Int,
    val lastWatchedDate: String
)
