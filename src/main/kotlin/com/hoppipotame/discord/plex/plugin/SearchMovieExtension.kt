package com.hoppipotame.discord.plex.plugin

import com.hoppipotame.discord.common.Plugin
import com.hoppipotame.discord.plex.domain.useCases.SearchMovieUseCase
import com.hoppipotame.discord.plex.plugin.commands.search.SearchMovieSlashCommand
import dev.kord.core.Kord


class SearchMovieExtension(
    private val searchMovieUseCase: SearchMovieUseCase,
) : Plugin {
    override suspend fun register(kord: Kord) {
        SearchMovieSlashCommand(searchMovieUseCase)
            .register(kord)
            .startListening()
    }
}