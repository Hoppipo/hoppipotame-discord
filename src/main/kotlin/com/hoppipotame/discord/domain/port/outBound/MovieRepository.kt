package com.hoppipotame.discord.domain.port.outBound

import com.hoppipotame.discord.domain.model.Movie
import com.hoppipotame.discord.domain.model.SearchQuery

interface MovieRepository {
    fun search(searchQuery: SearchQuery): List<Movie>
}