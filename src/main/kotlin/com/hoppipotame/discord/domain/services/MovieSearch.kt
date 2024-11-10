package com.hoppipotame.discord.domain.services

import com.hoppipotame.discord.domain.model.Movie
import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.port.inBound.SearchMovieUseCase
import com.hoppipotame.discord.domain.port.outBound.MovieRepository

class MovieSearch(private val searchMovieRepository: MovieRepository) : SearchMovieUseCase {
    override fun search(searchQuery: SearchQuery): List<Movie> =
        searchMovieRepository.search(searchQuery).take(5)
}