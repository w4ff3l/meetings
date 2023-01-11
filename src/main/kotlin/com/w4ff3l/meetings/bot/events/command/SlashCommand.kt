package com.w4ff3l.meetings.bot.events.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ImmutableApplicationCommandRequest
import reactor.core.publisher.Mono

interface SlashCommand {
    fun getName(): String
    fun getCommandRequest(): ImmutableApplicationCommandRequest
    fun handle(event: ChatInputInteractionEvent): Mono<Void>
}