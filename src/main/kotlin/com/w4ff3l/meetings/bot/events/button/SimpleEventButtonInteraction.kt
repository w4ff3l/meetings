package com.w4ff3l.meetings.bot.events.button

import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import reactor.core.publisher.Mono

interface SimpleEventButtonInteraction {
    fun getCustomId(): ButtonId
    fun handle(event: ButtonInteractionEvent): Mono<Void>
}