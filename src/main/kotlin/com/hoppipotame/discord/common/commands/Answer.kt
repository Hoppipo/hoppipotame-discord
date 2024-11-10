package com.hoppipotame.discord.common.commands

import dev.kord.core.entity.interaction.ChatInputCommandInteraction

interface Answer {
    suspend fun answer(interaction: ChatInputCommandInteraction)
}