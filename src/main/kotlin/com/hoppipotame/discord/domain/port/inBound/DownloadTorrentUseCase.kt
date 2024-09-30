package com.hoppipotame.discord.domain.port.inBound

import com.hoppipotame.discord.domain.model.HashQuery
import com.hoppipotame.discord.domain.model.MagnetQuery
import com.hoppipotame.discord.domain.model.Torrent

interface DownloadTorrentUseCase {
    fun downloadHash(hashQuery: HashQuery)
    fun downloadMagnet(magnetQuery: MagnetQuery)
    fun downloadMagnet(magnetQuery: Torrent)
}