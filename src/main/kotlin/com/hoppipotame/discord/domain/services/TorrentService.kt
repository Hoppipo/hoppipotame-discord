package com.hoppipotame.discord.domain.services

import com.hoppipotame.discord.domain.model.*
import com.hoppipotame.discord.domain.port.inBound.DownloadTorrentUseCase
import com.hoppipotame.discord.domain.port.inBound.DownloadTorrentFileUseCase
import com.hoppipotame.discord.domain.port.inBound.SearchTorrentUseCase
import com.hoppipotame.discord.domain.port.outBound.DemagnetizePort
import com.hoppipotame.discord.domain.services.catalog.TorrentCatalog

class TorrentService(
    private val catalog: TorrentCatalog,
    private val demagnetizePort: DemagnetizePort
) :
    SearchTorrentUseCase, DownloadTorrentFileUseCase, DownloadTorrentUseCase {
    override fun searchTorrent(searchQuery: SearchQuery): List<Torrent> =
        catalog.search(searchQuery)

    override fun searchAvailableConfigurations(id: String, ygg: TorrentSource): AvailableConfiguration {
        return AvailableConfiguration(
            listOf(
                TorrentQuality.HD, TorrentQuality.FULL_HD, TorrentQuality.UHD_4K
            )
        )
    }

    override fun downloadFromUrl(torrent: Torrent) {
        TODO("Not yet implemented")
    }

    override fun downloadHash(hashQuery: HashQuery) =
        demagnetizePort.fromHash(hashQuery)

    override fun downloadMagnet(magnetQuery: MagnetQuery) {
        demagnetizePort.fromMagnet(magnetQuery)
    }

    override fun downloadMagnet(magnetQuery: Torrent) {
        demagnetizePort.fromHash(HashQuery(magnetQuery.downloadData))
    }
}