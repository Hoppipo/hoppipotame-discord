package com.hoppipotame.discord.infrastructure.provider.catalog

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
private data class SearchResultItem(
    val id: String,
    val name: String,
    val info_hash: String,
    val leechers: String,
    val seeders: String,
    val num_files: String,
    val size: String,
    val username: String,
    val added: String,
    val status: String,
    val category: String,
    val imdb: String,
)

class PirateBayTorrentProvider(private val url: String, private val httpClient: HttpClient) : TorrentProvider {
    private val torrentSource = TorrentSource.THE_PIRATE_BAY

    override fun accept(source: TorrentSource): Boolean =
        source == torrentSource

    override fun search(searchQuery: SearchQuery): List<Torrent> {
        return runBlocking {
            httpClient.request(url + "/q.php?q=${searchQuery.query}&cat=200")
                .body<List<SearchResultItem>>()
        }
            .filter { item -> item.name != "No results returned" }
            .map { item -> Torrent(item.name, item.leechers.toInt(), item.seeders.toInt(), torrentSource) }
    }
}