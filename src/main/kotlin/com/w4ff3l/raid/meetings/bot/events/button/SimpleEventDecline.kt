package com.w4ff3l.raid.meetings.bot.events.button

import com.w4ff3l.raid.meetings.bot.events.SimpleEventMessageCreator
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SimpleEventDecline(private val simpleEventMessageCreator: SimpleEventMessageCreator) :
    SimpleEventButtonInteraction {
    val label = "Decline"

    override fun getCustomId(): ButtonId {
        return ButtonId.DECLINE
    }

    override fun handle(event: ButtonInteractionEvent): Mono<Void> {
        return event.edit().withEmbeds(simpleEventMessageCreator.modifyEmbedCreateSpec(event))
    }
}