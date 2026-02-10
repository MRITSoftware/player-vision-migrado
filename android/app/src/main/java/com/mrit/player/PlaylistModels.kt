package com.mrit.player

data class PlaylistItem(
    val url: String,
    val type: ItemType,
    val durationMs: Long? = null,
    val fit: String? = null,
    val focus: String? = null,
    val urlPortrait: String? = null,
    val urlLandscape: String? = null
)

enum class ItemType { VIDEO, IMAGE }

