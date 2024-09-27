package com.hoppipotame.discord.infrastructure.adapter

import com.hoppipotame.discord.domain.model.HashQuery
import com.hoppipotame.discord.domain.model.MagnetQuery
import com.hoppipotame.discord.domain.port.outBound.DemagnetizePort
import com.hoppipotame.discord.infrastructure.provider.ITorrentClient
import com.hoppipotame.discord.infrastructure.provider.MagnetToTorrentClient
import com.hoppipotame.discord.infrastructure.service.FileSaver
import kotlinx.coroutines.runBlocking

class ExternalDemagnetize(
    private val iTorrentClient: ITorrentClient,
    private val magnetToTorrentClient: MagnetToTorrentClient,
    private val fileSaver: FileSaver,
) : DemagnetizePort {
    override fun fromHash(hashQuery: HashQuery) {
        runBlocking {
            try {
                val torrentContent = iTorrentClient.toTorrent(hashQuery.hash)
                fileSaver.save("${hashQuery.hash}.torrent", torrentContent)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override fun fromMagnet(magnetQuery: MagnetQuery) {
        runBlocking {
            try {
                val response = magnetToTorrentClient.toTorrent(magnetQuery.magnet)
                fileSaver.save(response.fileName, response.content)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}