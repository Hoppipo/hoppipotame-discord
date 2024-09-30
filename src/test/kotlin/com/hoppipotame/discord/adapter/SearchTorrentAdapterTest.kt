package com.hoppipotame.discord.adapter

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource
import com.hoppipotame.discord.infrastructure.adapter.SearchTorrentAdapter
import com.hoppipotame.discord.infrastructure.provider.catalog.TorrentProvider
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FakeTorrentProvider(
    private val name: String,
    private val source: TorrentSource
) : TorrentProvider {
    override fun accept(source: TorrentSource): Boolean =
        this.source == source

    override fun search(searchQuery: SearchQuery): List<Torrent> =
        listOf(Torrent("", name, 0, 0, source))

}

class SearchTorrentAdapterTest {

    private lateinit var searchTorrentAdapter: SearchTorrentAdapter

    @Test
    fun `should return empty torrent list when there is no matching torrent provider`() {
        searchTorrentAdapter = SearchTorrentAdapter(emptyList())
        val searchResult = searchTorrentAdapter.searchTorrent(SearchQuery("Harry potter", 3), TorrentSource.YIFY)
        assertEquals(searchResult, emptyList())
    }

    @Test
    fun `should search in source torrent provider`() {
        val torrentName = "Harry potter"
        val torrentSource = TorrentSource.YIFY
        searchTorrentAdapter = SearchTorrentAdapter(
            listOf(
                FakeTorrentProvider(torrentName, torrentSource)
            )
        )
        val searchResult = searchTorrentAdapter.searchTorrent(SearchQuery(torrentName, 3), torrentSource)
        val expectedResult = listOf(Torrent("", torrentName, 0, 0, torrentSource))
        assertEquals(searchResult, expectedResult)
    }

    @Test
    fun `should only search in torrent provider of the given source`() {
        val torrentName = "Harry potter"
        val torrentSource = TorrentSource.YIFY
        searchTorrentAdapter = SearchTorrentAdapter(
            listOf(
                FakeTorrentProvider(torrentName, torrentSource),
                FakeTorrentProvider(torrentName, TorrentSource.OTHER)
            )
        )
        val searchResult = searchTorrentAdapter.searchTorrent(SearchQuery(torrentName, 3), torrentSource)
        val expectedResult = listOf(Torrent("", torrentName, 0, 0, torrentSource))
        assertEquals(searchResult, expectedResult)
    }
}