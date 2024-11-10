package com.hoppipotame.discord.domain.model


enum class TorrentSource(val displayName: String, val default: Boolean = false) {
    THE_PIRATE_BAY("The Pirate Bay"),
    YIFY("YIFY"),
    YGG("YGG", true),
}

enum class TorrentQuality(val quality: String) {
    HD("720"),
    FULL_HD("1080"),
    QHD("1440"),
    UHD_4K("2160"),
}


data class Torrent(val downloadData: String, val name: String, val leacher: Int?, val seeder: Int?, val source: TorrentSource)