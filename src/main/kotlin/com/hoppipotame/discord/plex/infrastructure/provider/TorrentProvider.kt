package com.hoppipotame.discord.plex.infrastructure.provider

import com.hoppipotame.discord.plex.domain.model.SearchQuery
import com.hoppipotame.discord.plex.domain.model.Torrent
import com.hoppipotame.discord.plex.domain.model.TorrentSource

interface TorrentProvider {
    fun accept(source: TorrentSource): Boolean
    fun search(searchQuery: SearchQuery): List<Torrent>
}