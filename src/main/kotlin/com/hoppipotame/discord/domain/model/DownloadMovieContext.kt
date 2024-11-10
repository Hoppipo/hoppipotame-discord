package com.hoppipotame.discord.domain.model

data class DownloadMovieContext(val id: String, val movie: Movie, val catalog: TorrentSource?, val quality: TorrentQuality?) {
    companion object {
        fun default(id: String, movie: Movie) =
            DownloadMovieContext(id, movie, null, null)
    }
}