package com.hoppipotame.discord.domain.services.catalog

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource
import com.hoppipotame.discord.domain.port.outBound.SearchTorrentPort
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class AggregateSourceCatalog(private val searchTorrentPort: SearchTorrentPort) : TorrentCatalog {
    override fun search(query: SearchQuery): List<Torrent> {
        return runBlocking {
            TorrentSource.entries.map { source ->
                async {
                    try {
                        searchTorrentPort.searchTorrent(query, source).take(query.size)
                    } catch (e: Exception) {
                        println(e.message)
                        emptyList()
                    }
                }
            }.awaitAll()
        }.flatten()
    }
}