package com.hoppipotame.discord.plex.domain.model

data class Movie(
    val id: String,
    val title: String,
    val releaseDate: String,
    val coverUrl: String,
    val adult: Boolean
)