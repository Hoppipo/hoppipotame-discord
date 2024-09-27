package com.hoppipotame.discord.infrastructure.provider

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking

class ITorrentClient(
    private val url: String,
    private val httpClient: HttpClient
) {
    fun toTorrent(hash: String): ByteArray {
        return runBlocking {
            return@runBlocking httpClient.request(url + "/torrent/${hash}.torrent")
                .bodyAsChannel()
                .toByteArray()
        }
    }
}