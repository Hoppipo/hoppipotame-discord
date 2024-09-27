package com.hoppipotame.discord.domain.port.inBound

import com.hoppipotame.discord.domain.model.HashQuery
import com.hoppipotame.discord.domain.model.MagnetQuery

interface DownloadTorrentUseCase {
    fun downloadHash(hashQuery: HashQuery)
    fun downloadMagnet(magnetQuery: MagnetQuery)
}