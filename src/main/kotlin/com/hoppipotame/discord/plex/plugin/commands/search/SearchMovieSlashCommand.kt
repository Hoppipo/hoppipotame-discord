package com.hoppipotame.discord.plex.plugin.commands.search

import com.hoppipotame.discord.common.commands.SlashCommand
import com.hoppipotame.discord.plex.domain.model.SearchQuery
import com.hoppipotame.discord.plex.domain.useCases.SearchMovieUseCase
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.GlobalChatInputCommandInteraction
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.GlobalChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import io.ktor.util.logging.*

class SearchMovieSlashCommand(
    private val searchMovieUseCase: SearchMovieUseCase
) : SlashCommand {

    private lateinit var kord: Kord;


    override suspend fun register(kord: Kord): SlashCommand {
        this.kord = kord
        LOGGER.info("Registering SearchMovieSlashCommand")
        kord.createGlobalChatInputCommand(
            name = "search",
            description = "Search"
        ) {
            subCommand("movie", "Search for a movie") {
                string("title", "The full or partial name of the movie you're looking for.") {
                    required = true
                }
                string("language", "The title's language. By default, the original title is displayed.") {
                    required = false
                    choice("French", FRENCH)
                    choice("English (US)", "en-US")
                }
            }
        }
        return this
    }

    override suspend fun startListening(): SlashCommand {
        LOGGER.info("Listening started for SearchMovieSlashCommand")
        kord.on<GlobalChatInputCommandInteractionCreateEvent> {
            when (val command = interaction.command) {
                is SubCommand -> if (command.name == "movie" && command.rootName == "search") searchForMovie(interaction, command.strings["title"]!!, command.strings["language"] ?: DEFAULT_LANGUAGE)
                else -> Unit
            }
        }
        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            when (val command = interaction.command) {
                is SubCommand -> if (command.name == "movie" && command.rootName == "search") searchForMovie(interaction, command.strings["title"]!!, command.strings["language"] ?: DEFAULT_LANGUAGE)
                else -> Unit
            }
        }
        return this
    }

    private suspend fun searchForMovie(
        interaction: ChatInputCommandInteraction,
        searchQuery: String,
        searchLanguage: String
    ) {
        val movies = searchMovieUseCase.search(
            SearchQuery(
                query = searchQuery,
                size = 6,
                language = searchLanguage
            )
        )
        SearchMovieResponse(searchQuery, movies).answer(interaction)
    }

    companion object {
        internal val FRENCH = "fr"
        internal val DEFAULT_LANGUAGE = FRENCH
        internal val LOGGER = KtorSimpleLogger(SearchMovieSlashCommand::class.qualifiedName!!)
    }
}