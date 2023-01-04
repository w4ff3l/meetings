package com.w4ff3l.raid.meetings.bot.listener

import com.w4ff3l.raid.meetings.bot.events.command.SlashCommand
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

@Component
class SlashCommandListener(private val commands: List<SlashCommand>) : EventListener<ChatInputInteractionEvent> {
    override fun getEventType(): KClass<ChatInputInteractionEvent> {
        return ChatInputInteractionEvent::class
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return Flux.fromIterable(commands)
            .filter { it.getName() == event.commandName }
            .next()
            .flatMap { it.handle(event) }
    }
}