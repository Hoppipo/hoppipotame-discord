package com.hoppipotame.discord.domain.port.inBound

import com.hoppipotame.discord.domain.model.Movie
import com.hoppipotame.discord.domain.model.TorrentQuality

interface AddOnPlexUseCase {
    fun init(movie: Movie)
    fun quality(id: String, quality: TorrentQuality)
}