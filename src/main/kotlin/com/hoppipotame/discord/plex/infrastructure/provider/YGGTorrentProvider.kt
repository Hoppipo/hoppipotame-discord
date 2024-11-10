package com.hoppipotame.discord.plex.infrastructure.provider

import com.hoppipotame.discord.plex.domain.model.SearchQuery
import com.hoppipotame.discord.plex.domain.model.Torrent
import com.hoppipotame.discord.plex.domain.model.TorrentSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class YGGSearchResultItem(
    val id: Int,
    val title: String,
    val leechers: Int,
    val seeders: Int,
    val size: String,
    val slug: String,
)

class YGGTorrentProvider(private val url: String, private val httpClient: HttpClient) : TorrentProvider {
    private val torrentSource = TorrentSource.YGG

    override fun accept(source: TorrentSource): Boolean =
        source == torrentSource

    override fun search(searchQuery: SearchQuery): List<Torrent> {
        return runBlocking {
            httpClient.request(url + "/torrents?q=${searchQuery.query}&order_by=seeders")
                .body<List<YGGSearchResultItem>>()
        }
            .map { item ->
                Torrent(
                    item.id.toString(),
                    item.title,
                    item.leechers,
                    item.seeders,
                    torrentSource
                )
            }
    }
}