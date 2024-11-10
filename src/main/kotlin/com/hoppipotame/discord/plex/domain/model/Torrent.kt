package com.hoppipotame.discord.plex.domain.model


enum class TorrentSource(val displayName: String, val default: Boolean = false) {
    THE_PIRATE_BAY("The Pirate Bay"),
    YIFY("YIFY"),
    YGG("YGG", true),
}


data class Torrent(val downloadData: String, val name: String, val leacher: Int?, val seeder: Int?, val source: TorrentSource)