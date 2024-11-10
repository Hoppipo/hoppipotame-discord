package com.hoppipotame.discord.services.catalog

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource
import com.hoppipotame.discord.domain.port.outBound.SearchTorrentPort
import com.hoppipotame.discord.domain.services.catalog.AggregateSourceCatalog
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IdentifySearchTorrentPort(private val movieName: String) : SearchTorrentPort {
    override fun searchTorrent(searchQuery: SearchQuery, source: TorrentSource): List<Torrent> =
        listOf(Torrent("", movieName, 0, 0, source))
}

class AggregateSourceCatalogTest {

    private lateinit var torrentCatalogAdapter: AggregateSourceCatalog

    @Test
    fun `should aggregate search result from all torrent source`() {
        val movieName = "Harry potter"
        torrentCatalogAdapter = AggregateSourceCatalog(IdentifySearchTorrentPort(movieName))
        val aggregatedSearchResult = torrentCatalogAdapter.search(SearchQuery(movieName))
        val expectedAggregateResult = TorrentSource.entries.map { source -> Torrent("", movieName, 0, 0, source) }
        assertEquals(aggregatedSearchResult, expectedAggregateResult)
    }
}