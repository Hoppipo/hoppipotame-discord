package com.hoppipotame.discord.infrastructure.provider.catalog

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource

interface TorrentProvider {
    fun accept(source: TorrentSource): Boolean
    fun search(searchQuery: SearchQuery): List<Torrent>
}