package com.hoppipotame.discord.domain.services

import com.hoppipotame.discord.domain.model.DownloadMovieContext
import com.hoppipotame.discord.domain.model.Movie

class DownloadContextHolder {
    private val contextMap: MutableMap<String, DownloadMovieContext> = mutableMapOf()

    fun create(id: String, movie: Movie) {
        contextMap[id] = DownloadMovieContext.default(id, movie)
    }

    fun find(id: String): DownloadMovieContext? {
        return contextMap[id]
    }

    fun update(id: String, context: DownloadMovieContext) {
        contextMap[id] = context
    }

}