package com.hoppipotame.discord

import com.hoppipotame.discord.domain.model.HashQuery
import com.hoppipotame.discord.domain.model.MagnetQuery
import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.services.TorrentService
import com.hoppipotame.discord.domain.services.catalog.AggregateSourceCatalog
import com.hoppipotame.discord.infrastructure.adapter.ExternalDemagnetize
import com.hoppipotame.discord.infrastructure.adapter.SearchTorrentAdapter
import com.hoppipotame.discord.infrastructure.provider.ITorrentClient
import com.hoppipotame.discord.infrastructure.provider.MagnetToTorrentClient
import com.hoppipotame.discord.infrastructure.provider.catalog.PirateBayTorrentProvider
import com.hoppipotame.discord.infrastructure.provider.catalog.YifyTorrentProvider
import com.hoppipotame.discord.infrastructure.service.FileSaver
import dev.kord.common.entity.ButtonStyle.Secondary
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.reply
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.message.actionRow
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

private val httpClient = HttpClient {
    install(Logging){
        level = LogLevel.ALL
        logger = Logger.DEFAULT
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

private val messageDataMap: MutableMap<Snowflake, Torrent> = mutableMapOf()

private val searchTorrentUserCase: TorrentService = TorrentService(
    catalog = AggregateSourceCatalog(
        searchTorrentPort = SearchTorrentAdapter(
            torrentProviders = listOf(
                YifyTorrentProvider("https://yts.am", httpClient),
                PirateBayTorrentProvider("https://apibay.org", httpClient)
            )
        )
    ),
    demagnetizePort = ExternalDemagnetize(
        iTorrentClient = ITorrentClient("https://itorrents.org", httpClient),
        magnetToTorrentClient = MagnetToTorrentClient("https://magnet2torrent.com", httpClient),
        fileSaver = FileSaver(
            torrentFolder = System.getenv("HOPPIPOTAME_DOWNLOAD_FOLDER") ?: "torrent"
        ),
    )
)

suspend fun main() {

    val searchSize = System.getenv("HOPPIPOTAME_SEARCH_RESULT_SIZE")?.toInt() ?: 10
    val token = System.getenv("HOPPIPOTAME_DISCORD_TOKEN")
    val kord = Kord(token)

    kord.on<ButtonInteractionCreateEvent> {
        when (interaction.data.data.customId.value) {
            "download_torrent" -> handleDownloadTorrent(interaction)
            else -> {}
        }
    }

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot != false) return@on
        val words = message.content.split(" ")
        when (words.firstOrNull()) {
            "!search" -> search(words.drop(1).joinToString(" "), searchSize)
            "!hash" -> downloadHash(words.drop(1).joinToString(" "))
            "!magnet" -> downloadMagnet(words.drop(1).joinToString(" "))
        }
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}

private suspend fun MessageCreateEvent.downloadMagnet(magnet: String) {
    val reply = message.reply {
        suppressNotifications = true
        content = "⏳ In progress... "
    }
    val magnetQuery = MagnetQuery(magnet)
    searchTorrentUserCase.downloadMagnet(magnetQuery)
    reply.edit {
        content = "✅ Finished"
    }
}

private suspend fun MessageCreateEvent.downloadHash(hash: String) {
    val reply = message.reply {
        suppressNotifications = true
        content = "⏳ In progress... "
    }
    val hashQuery = HashQuery(hash)
    searchTorrentUserCase.downloadHash(hashQuery)
    reply.edit {
        content = "✅ Finished"
    }
}

suspend fun handleDownloadTorrent(buttonInteraction: ButtonInteraction) {
    buttonInteraction.message.channel.getMessage(buttonInteraction.message.id).edit {
        actionRow {
            interactionButton(Secondary, "downloading") {
                disabled = true
                label = "⏳ In progress... "
            }
        }
    }
    buttonInteraction.deferPublicMessageUpdate()
    delay(2000)
    buttonInteraction.message.channel.getMessage(buttonInteraction.message.id).edit {
        actionRow {
            interactionButton(Secondary, "downloaded") {
                disabled = true
                label = "✅ Finished"
            }
        }
    }
}

private suspend fun MessageCreateEvent.search(query: String, size: Int) {
    val searchTorrent = searchTorrentUserCase.searchTorrent(SearchQuery(query, size))
    if (searchTorrent.isEmpty()) {
        message.reply {
            suppressNotifications = true
            content = "No result \uD83D\uDE1F"
        }
    }
    searchTorrent.forEachIndexed { index, torrent ->
        val messageCreated = message.channel.createMessage {
            suppressNotifications = true
            content = "${index + 1}. ${torrent.name}"
            actionRow {
                interactionButton(Secondary, "download_torrent") {
                    label = "Get from ${torrent.source.displayName}"
                }
            }
        }
        messageDataMap[messageCreated.id] = torrent
    }
}