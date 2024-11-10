package com.hoppipotame.discord.plex.domain.adapters

import com.hoppipotame.discord.plex.domain.model.Movie
import com.hoppipotame.discord.plex.domain.model.SearchQuery
import com.hoppipotame.discord.plex.domain.useCases.SearchMovieUseCase
import com.hoppipotame.discord.plex.domain.outBound.MovieRepository

class MovieSearch(private val searchMovieRepository: MovieRepository) : SearchMovieUseCase {
    override fun search(searchQuery: SearchQuery): List<Movie> =
        searchMovieRepository.search(searchQuery).take(searchQuery.size)
}