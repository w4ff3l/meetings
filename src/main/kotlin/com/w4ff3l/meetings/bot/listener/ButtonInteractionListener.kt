package com.w4ff3l.meetings.bot.listener

import com.w4ff3l.meetings.bot.events.button.SimpleEventButtonInteraction
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

@Component
class ButtonInteractionListener(
    private val buttonInteractions: List<SimpleEventButtonInteraction>
) : EventListener<ButtonInteractionEvent> {
    override fun getEventType(): KClass<ButtonInteractionEvent> {
        return ButtonInteractionEvent::class
    }

    override fun handle(event: ButtonInteractionEvent): Mono<Void> {
        return Flux.fromIterable(buttonInteractions)
            .filter { interaction -> interaction.getCustomId().id == event.customId }
            .next()
            .flatMap { interaction -> interaction.handle(event) }
    }
}
