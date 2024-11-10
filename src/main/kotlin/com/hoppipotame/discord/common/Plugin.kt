package com.hoppipotame.discord.common

import dev.kord.core.Kord

interface Plugin {
    suspend fun register(kord: Kord)
}