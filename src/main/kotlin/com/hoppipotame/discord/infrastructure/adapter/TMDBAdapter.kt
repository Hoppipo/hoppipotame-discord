package com.hoppipotame.discord.infrastructure.adapter

import com.hoppipotame.discord.domain.model.Movie
import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.port.outBound.MovieRepository
import com.hoppipotame.discord.infrastructure.provider.TMDBClient

class TMDBAdapter(private val tmdbClient: TMDBClient) : MovieRepository {
    override fun search(searchQuery: SearchQuery): List<Movie> =
        tmdbClient.search(searchQuery.query, searchQuery.language)
}