package com.hoppipotame.discord.domain.port.inBound

import com.hoppipotame.discord.domain.model.AvailableConfiguration
import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource

interface SearchTorrentUseCase {
    fun searchTorrent(searchQuery: SearchQuery): List<Torrent>
    fun searchAvailableConfigurations(id: String, ygg: TorrentSource): AvailableConfiguration
}