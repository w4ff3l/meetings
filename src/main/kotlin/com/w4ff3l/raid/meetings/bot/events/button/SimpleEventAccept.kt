package com.w4ff3l.raid.meetings.bot.events.button

import com.w4ff3l.raid.meetings.bot.events.SimpleEventMessageCreator
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SimpleEventAccept(private val simpleEventMessageCreator: SimpleEventMessageCreator) :
    SimpleEventButtonInteraction {
    val label = "Accept"

    override fun getCustomId(): ButtonId {
        return ButtonId.ACCEPT
    }

    override fun handle(event: ButtonInteractionEvent): Mono<Void> {
        return event.edit().withEmbeds(simpleEventMessageCreator.modifyEmbedCreateSpec(event))
    }
}