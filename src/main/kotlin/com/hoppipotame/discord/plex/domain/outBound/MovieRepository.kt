package com.hoppipotame.discord.plex.domain.outBound

import com.hoppipotame.discord.plex.domain.model.Movie
import com.hoppipotame.discord.plex.domain.model.SearchQuery

interface MovieRepository {
    fun search(searchQuery: SearchQuery): List<Movie>
}