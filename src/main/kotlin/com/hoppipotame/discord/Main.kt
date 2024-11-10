package com.hoppipotame.discord

import com.hoppipotame.discord.domain.port.inBound.AddOnPlexUseCase
import com.hoppipotame.discord.domain.port.inBound.SearchMovieUseCase
import com.hoppipotame.discord.domain.services.AddOnPlexService
import com.hoppipotame.discord.domain.services.DownloadContextHolder
import com.hoppipotame.discord.domain.services.MovieSearch
import com.hoppipotame.discord.domain.services.TorrentService
import com.hoppipotame.discord.domain.services.catalog.AggregateSourceCatalog
import com.hoppipotame.discord.infrastructure.adapter.ExternalDemagnetize
import com.hoppipotame.discord.infrastructure.adapter.SearchTorrentAdapter
import com.hoppipotame.discord.infrastructure.adapter.TMDBAdapter
import com.hoppipotame.discord.infrastructure.extensions.Extension
import com.hoppipotame.discord.infrastructure.extensions.LegacyExtension
import com.hoppipotame.discord.infrastructure.extensions.MinecraftExtension
import com.hoppipotame.discord.infrastructure.extensions.plex.SearchMovieExtension
import com.hoppipotame.discord.infrastructure.extensions.plex.AddOnPlexExtension
import com.hoppipotame.discord.infrastructure.provider.ITorrentClient
import com.hoppipotame.discord.infrastructure.provider.MagnetToTorrentClient
import com.hoppipotame.discord.infrastructure.provider.TMDBClient
import com.hoppipotame.discord.infrastructure.provider.catalog.PirateBayTorrentProvider
import com.hoppipotame.discord.infrastructure.provider.catalog.YGGTorrentProvider
import com.hoppipotame.discord.infrastructure.provider.catalog.YifyTorrentProvider
import com.hoppipotame.discord.infrastructure.service.FileSaver
import com.typesafe.config.ConfigFactory
import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private val httpClient = HttpClient {
    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.DEFAULT
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

val config = ConfigFactory.load("application.conf")

suspend fun Kord.register(vararg extensions: Extension) {
    extensions.forEach { extension -> extension.register(this) }
}

suspend fun main() {
    val kord = Kord(config.getString("discord.token"))

    val searchMovieUseCase: SearchMovieUseCase = MovieSearch(
        searchMovieRepository = TMDBAdapter(
            tmdbClient = TMDBClient(config.getConfig("tmdb"), httpClient)
        )
    )
    val addOnPlexUseCase: AddOnPlexUseCase = AddOnPlexService(
        downloadContextHolder = DownloadContextHolder()
    )

    val searchTorrentUserCase = TorrentService(
        catalog = AggregateSourceCatalog(
            searchTorrentPort = SearchTorrentAdapter(
                torrentProviders = listOf(
                    YGGTorrentProvider("https://yggapi.eu", httpClient),
                    YifyTorrentProvider("https://yts.am", httpClient),
                    PirateBayTorrentProvider("https://apibay.org", httpClient)
                )
            )
        ),
        demagnetizePort = ExternalDemagnetize(
            iTorrentClient = ITorrentClient("https://itorrents.org", httpClient),
            magnetToTorrentClient = MagnetToTorrentClient("https://magnet2torrent.com", httpClient),
            fileSaver = FileSaver(
                torrentFolder = "/downloads"
            ),
        )
    )


    kord.register(
        SearchMovieExtension(searchMovieUseCase, addOnPlexUseCase),
        MinecraftExtension(),
        AddOnPlexExtension(addOnPlexUseCase, searchTorrentUserCase)
    )

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
