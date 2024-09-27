package com.hoppipotame.discord.infrastructure.provider

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

data class MagnetToTorrentClientResponse(val fileName: String, val content: ByteArray)

class MagnetToTorrentClient(
    private val url: String,
    private val httpClient: HttpClient
) {
    fun toTorrent(magnet: String): MagnetToTorrentClientResponse {
        return runBlocking {
            val response = httpClient.submitForm(
                url = "${url}/upload/index.php",
                formParameters = Parameters.build {
                    append("magnet", magnet)
                }
            )
            return@runBlocking MagnetToTorrentClientResponse(
                fileName = response.headers[HttpHeaders.ContentDisposition]?.let(ContentDisposition.Companion::parse)?.parameter("filename") ?: "$magnet.torrent",
                content =  response.body())
        }
    }
}