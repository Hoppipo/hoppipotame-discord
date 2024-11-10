package com.hoppipotame.discord.infrastructure.extensions

import dev.kord.core.Kord

interface Extension {
    suspend fun register(kord: Kord)
}

data class CustomId(val command: String, val data: Map<String, String>) {
    override fun toString(): String {
        return "$command$commandDataDelimiter${data.entries.joinToString(dataDelimiter) { (key, value) -> "$key$dataAssignation$value" }}"
    }

    companion object {
        const val commandDataDelimiter = "|"
        const val dataDelimiter = ":&:"
        const val dataAssignation = ":~:"

        fun from(customId: String): CustomId {
            val (command, rest) = customId.split(commandDataDelimiter, limit = 2)
            val dataMap = rest.split(dataDelimiter)
                .map { data -> data.split(dataAssignation, limit = 2) }
                .associate { pair -> pair[0] to pair[1] }
            return CustomId(command, dataMap)
        }
    }
}