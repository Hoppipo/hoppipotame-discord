package com.hoppipotame.discord.domain.model


enum class TorrentSource(val displayName: String) {
    THE_PIRATE_BAY("The Pirate Bay"),
    YIFY("YIFY"),
    OTHER("Other")
}

data class Torrent(val downloadData: String, val name: String, val leacher: Int?, val seeder: Int?, val source: TorrentSource)