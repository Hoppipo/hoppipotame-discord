package com.hoppipotame.discord.plex.plugin

import com.hoppipotame.discord.common.Plugin
import com.hoppipotame.discord.config
import com.hoppipotame.discord.plex.domain.adapters.MovieSearch
import com.hoppipotame.discord.plex.infrastructure.adapter.TMDBAdapter
import com.hoppipotame.discord.plex.infrastructure.provider.TMDBClient
import dev.kord.core.Kord
import io.ktor.client.*


class PlexPlugin(httpClient: HttpClient) : Plugin {

    private val plugins: List<Plugin> = listOf(
        SearchMovieExtension(
            searchMovieUseCase = MovieSearch(
                searchMovieRepository = TMDBAdapter(
                    tmdbClient = TMDBClient(config.getConfig("tmdb"), httpClient)
                )
            )
        )
    )

    override suspend fun register(kord: Kord) {
        plugins.forEach { plugin -> plugin.register(kord) }
    }
}