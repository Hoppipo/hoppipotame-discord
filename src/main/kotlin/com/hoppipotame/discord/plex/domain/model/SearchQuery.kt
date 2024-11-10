package com.hoppipotame.discord.plex.domain.model

data class SearchQuery(val query: String, val size: Int = 0, val language: String? = null)