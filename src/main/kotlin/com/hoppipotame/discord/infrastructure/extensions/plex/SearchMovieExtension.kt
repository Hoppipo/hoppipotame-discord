package com.hoppipotame.discord.infrastructure.extensions.plex

import com.hoppipotame.discord.config
import com.hoppipotame.discord.domain.model.Movie
import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.port.inBound.AddOnPlexUseCase
import com.hoppipotame.discord.domain.port.inBound.SearchMovieUseCase
import com.hoppipotame.discord.domain.port.inBound.SearchTorrentUseCase
import com.hoppipotame.discord.infrastructure.extensions.CustomId
import com.hoppipotame.discord.infrastructure.extensions.Extension
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.interaction.GlobalChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed


class SearchMovieExtension(
    private val searchMovieUseCase: SearchMovieUseCase,
    private val addOnPlexUseCase: AddOnPlexUseCase
) : Extension {
    override suspend fun register(kord: Kord) {
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
                    choice("French", "fr")
                    choice("English (US)", "en-US")
                }
            }
        }

        kord.on<GlobalChatInputCommandInteractionCreateEvent> {
            val response = interaction.deferEphemeralResponse()
            val command = interaction.command
            val movies = searchMovieUseCase.search(
                SearchQuery(
                    query = command.strings["title"]!!,
                    language = command.strings["language"]
                )
            )
            if (movies.isNotEmpty()) {
                response.respond {
                    content = """
                        :mag: ${command.strings["title"]} :mag:
                        :arrow_down: Voila ce que j'ai trouvé :arrow_down:
                        """.trimIndent()
                }
                val dmChannel = interaction.user.getDmChannel()
                sendResultToChannel(movies, dmChannel)
            } else {
                response.respond {
                    content = "Oups :speak_no_evil: Je n'ai rien trouvé..."
                }
            }
        }

        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            val response = interaction.deferEphemeralResponse()
            val command = interaction.command
            val movies = searchMovieUseCase.search(
                SearchQuery(
                    query = command.strings["title"]!!,
                    language = command.strings["language"]
                )
            )
            if (movies.isNotEmpty()) {
                response.respond {
                    content = "Jettes un œil à tes DM :detective:"
                }
                val dmChannel = interaction.user.getDmChannel()
                dmChannel.createMessage {
                    suppressNotifications = true
                    content = ":arrow_down: Voila ce que j'ai trouvé :arrow_down:"
                }
                sendResultToChannel(movies, dmChannel)
            } else {
                response.respond {
                    content = "Oups :speak_no_evil: Je n'ai rien trouvé..."
                }
            }
        }
    }

    private suspend fun sendResultToChannel(search: List<Movie>, channel: MessageChannel) {
        search.forEach { movie ->
            addOnPlexUseCase.init(movie)
            val customId = CustomId(
                addOnPlex,
                mapOf("id" to movie.id, "title" to movie.title)
            )
            channel.createMessage {
                suppressNotifications = true
                embed {
                    thumbnail {
                        url = movie.coverUrl
                    }
                    author {
                        url = config.getString("tmdb.website") + "/movie/${movie.id}"
                        name = movie.title
                        icon = config.getString("tmdb.icon")
                    }
                    field {
                        name = "Release date"
                        value = movie.releaseDate
                    }
                    field {
                        name = "Adult"
                        value = if (movie.adult) "\uD83D\uDD1E" else "No"
                    }
                }
                actionRow {
                    interactionButton(ButtonStyle.Primary, customId.toString()) {
                        disabled = false
                        label = "Add on PLEX"
                    }
                    linkButton(config.getString("tmdb.website") + "/movie/${movie.id}") {
                        label = "View on TMDB"
                    }
                }
            }
        }
    }
}