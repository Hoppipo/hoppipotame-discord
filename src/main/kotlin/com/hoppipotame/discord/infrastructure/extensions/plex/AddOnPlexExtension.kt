package com.hoppipotame.discord.infrastructure.extensions.plex

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.port.inBound.AddOnPlexUseCase
import com.hoppipotame.discord.domain.port.inBound.SearchTorrentUseCase
import com.hoppipotame.discord.infrastructure.extensions.CustomId
import com.hoppipotame.discord.infrastructure.extensions.Extension
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.response.EphemeralMessageInteractionResponse
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on

const val addOnPlex = "add_on_plex"
const val selectQuality = "select_quality"
const val selectCatalog = "select_catalog"

class AddOnPlexExtension(
    private val addOnPlexUseCase: AddOnPlexUseCase,
    private val searchTorrentUseCase: SearchTorrentUseCase
) : Extension {
    private val responses = mutableMapOf<Snowflake, EphemeralMessageInteractionResponse>()

    override suspend fun register(kord: Kord) {
        kord.on<ButtonInteractionCreateEvent> {
            val customId = CustomId.from(interaction.data.data.customId.value!!)
            when (customId.command) {
                addOnPlex -> handleAddOnPlex(customId)
            }
        }

        kord.on<SelectMenuInteractionCreateEvent> {
            val customId = CustomId.from(interaction.data.data.customId.value!!)

            when (customId.command) {
                selectCatalog -> handleChange(customId)
            }
        }
    }

    private fun handleChange(customId: CustomId) {
        println("ICI ca handle change" + customId)
    }

    private suspend fun ButtonInteractionCreateEvent.handleAddOnPlex(customId: CustomId) {
        val response = interaction.deferEphemeralResponse()
        val title = customId.data["title"]!!
        val id = customId.data["id"]!!
        val torrents = searchTorrentUseCase.searchTorrent(SearchQuery(title)).take(25)
        val realResponse = response.respond {
            content = torrents.mapIndexed { index, torrent -> "$index. ${torrent.name}" }.joinToString("\n")
        }
        responses[realResponse.message.id] = realResponse
    }
}