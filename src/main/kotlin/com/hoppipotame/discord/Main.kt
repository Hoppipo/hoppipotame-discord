package com.hoppipotame.discord

import com.hoppipotame.discord.common.Plugin
import com.hoppipotame.discord.minecraft.plugin.MinecraftPlugin
import com.hoppipotame.discord.plex.plugin.PlexPlugin
import com.typesafe.config.ConfigFactory
import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private val httpClient = HttpClient {
    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.DEFAULT
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

val config = ConfigFactory.load("application.conf")

suspend fun Kord.register(vararg plugins: Plugin) {
    plugins.forEach { plugin -> plugin.register(this) }
}

suspend fun main() {
    val kord = Kord(config.getString("discord.token"))

    kord.register(
        PlexPlugin(httpClient),
        MinecraftPlugin()
    )

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
