package com.hoppipotame.discord.infrastructure.service

import java.io.File

class FileSaver(
    private val torrentFolder: String,
) {
    fun save(fileName: String, content: ByteArray) {
        File("$torrentFolder/$fileName").writeBytes(content)
    }
}