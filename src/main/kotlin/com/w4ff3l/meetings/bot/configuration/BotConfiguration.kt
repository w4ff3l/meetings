package com.w4ff3l.meetings.bot.configuration

import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BotConfiguration(@Value("\${discord.token}") private val token: String) {
    @Bean
    fun discordClient(): DiscordClient {
        return DiscordClientBuilder.create(token).build()
    }
}

