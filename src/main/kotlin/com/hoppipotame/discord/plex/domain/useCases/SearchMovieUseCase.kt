package com.hoppipotame.discord.plex.domain.useCases

import com.hoppipotame.discord.plex.domain.model.Movie
import com.hoppipotame.discord.plex.domain.model.SearchQuery

interface SearchMovieUseCase {
    fun search(searchQuery: SearchQuery): List<Movie>
}