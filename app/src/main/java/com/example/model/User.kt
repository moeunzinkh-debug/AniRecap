package com.example.model

enum class UserRole(val label: String, val badgeColorHex: Long) {
    VIEWER("Viewer", 0xFF06B6D4),
    CREATOR("Creator", 0xFF8B5CF6),
    ADMIN("Admin", 0xFFFF2E4D)
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val role: UserRole,
    val channelName: String? = null,
    val subscribersCount: Int = 0,
    val bio: String = ""
)
