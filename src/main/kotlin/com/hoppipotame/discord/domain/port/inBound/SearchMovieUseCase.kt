package com.hoppipotame.discord.domain.port.inBound

import com.hoppipotame.discord.domain.model.Movie
import com.hoppipotame.discord.domain.model.SearchQuery

interface SearchMovieUseCase {
    fun search(searchQuery: SearchQuery): List<Movie>
}