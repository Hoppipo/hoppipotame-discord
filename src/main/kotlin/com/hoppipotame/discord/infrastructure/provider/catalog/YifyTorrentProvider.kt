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
private data class YifySearchResult(val data: List<YifySearchResultItem>)

@Serializable
private data class YifySearchResultItem(
    val url: String,
    val img: String,
    val title: String,
    val year: String
)

class YifyTorrentProvider(private val url: String, private val httpClient: HttpClient) : TorrentProvider {
    private val torrentSource = TorrentSource.YIFY

    override fun accept(source: TorrentSource): Boolean =
        source == torrentSource

    override fun search(searchQuery: SearchQuery): List<Torrent> {
        return runBlocking {
            httpClient.request(url + "/ajax/search?query=${searchQuery.query}")
                .body<YifySearchResult>()
        }.data.map { item -> Torrent(item.title, null, null, torrentSource) }
    }
}