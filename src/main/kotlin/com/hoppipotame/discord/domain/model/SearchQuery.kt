package com.hoppipotame.discord.domain.model

data class SearchQuery(val query: String, val size: Int = 0, val language: String? = null)