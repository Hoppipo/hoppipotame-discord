package com.hoppipotame.discord.plex.plugin.commands.search

import com.hoppipotame.discord.common.commands.Answer
import com.hoppipotame.discord.config
import com.hoppipotame.discord.plex.domain.model.Movie
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.response.DeferredEphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.GlobalChatInputCommandInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed

class SearchMovieResponse(private val searchQuery: String, private val movies: List<Movie>) : Answer {
    override suspend fun answer(interaction: ChatInputCommandInteraction) {
        val response = interaction.deferEphemeralResponse()
        if (movies.isNotEmpty()) {
            when (interaction) {
                is GlobalChatInputCommandInteraction -> response.replaySearchResume()
                is GuildChatInputCommandInteraction -> response.replyGoToDM()
            }
            val userDM = interaction.user.getDmChannel().asChannel()
            sendResultToChannel(userDM)
        } else {
            response.replyEmpty()
        }
    }

    private suspend fun DeferredEphemeralMessageInteractionResponseBehavior.replyEmpty() {
        this.respond { content = "Oups :speak_no_evil: Je n'ai rien trouvé..." }
    }

    private suspend fun DeferredEphemeralMessageInteractionResponseBehavior.replyGoToDM() {
        this.respond { content = "Jettes un œil à tes DM :detective:" }
    }

    private suspend fun DeferredEphemeralMessageInteractionResponseBehavior.replaySearchResume() {
        this.respond {
            content = """
                        :mag: $searchQuery :mag:
                        :arrow_down: Voila ce que j'ai trouvé :arrow_down:
                        """.trimIndent()
        }
    }

    private suspend fun sendResultToChannel(channel: MessageChannel) {
        movies.forEach { movie ->
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
                    interactionButton(ButtonStyle.Primary, movie.id) {
                        disabled = true
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