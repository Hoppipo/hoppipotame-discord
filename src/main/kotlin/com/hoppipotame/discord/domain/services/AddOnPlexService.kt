package com.hoppipotame.discord.domain.services

import com.hoppipotame.discord.domain.model.Movie
import com.hoppipotame.discord.domain.model.TorrentQuality
import com.hoppipotame.discord.domain.port.inBound.AddOnPlexUseCase

class AddOnPlexService(private val downloadContextHolder: DownloadContextHolder) : AddOnPlexUseCase {
    override fun init(movie: Movie) =
        downloadContextHolder.create(movie.id, movie)

    override fun quality(id: String, quality: TorrentQuality) {
        downloadContextHolder.find(id)?.let { context ->
            val updatedContext = context.copy(quality = quality)
            downloadContextHolder.update(id, updatedContext)
        }
    }
}