package com.hoppipotame.discord.plex.infrastructure.adapter

import com.hoppipotame.discord.plex.domain.outBound.MovieRepository
import com.hoppipotame.discord.plex.infrastructure.provider.TMDBClient
import com.hoppipotame.discord.plex.domain.model.Movie
import com.hoppipotame.discord.plex.domain.model.SearchQuery

class TMDBAdapter(private val tmdbClient: TMDBClient) : MovieRepository {
    override fun search(searchQuery: SearchQuery): List<Movie> =
        tmdbClient.search(searchQuery.query, searchQuery.language)
}