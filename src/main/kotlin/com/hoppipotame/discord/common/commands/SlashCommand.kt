package com.hoppipotame.discord.common.commands

import dev.kord.core.Kord

interface SlashCommand {
    suspend fun register(kord: Kord): SlashCommand
    suspend fun startListening(): SlashCommand
}