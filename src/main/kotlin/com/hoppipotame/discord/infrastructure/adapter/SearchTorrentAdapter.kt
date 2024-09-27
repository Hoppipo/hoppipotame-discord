package com.hoppipotame.discord.infrastructure.adapter

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource
import com.hoppipotame.discord.domain.port.outBound.SearchTorrentPort
import com.hoppipotame.discord.infrastructure.provider.catalog.TorrentProvider

class SearchTorrentAdapter(private val torrentProviders: List<TorrentProvider>) : SearchTorrentPort {
    override fun searchTorrent(searchQuery: SearchQuery, source: TorrentSource): List<Torrent> {
        return torrentProviders
            .filter { torrentProviders -> torrentProviders.accept(source) }
            .flatMap { torrentProvider -> torrentProvider.search(searchQuery) }
    }
}