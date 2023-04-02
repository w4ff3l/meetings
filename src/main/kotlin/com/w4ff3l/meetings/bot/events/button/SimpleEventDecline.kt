package com.w4ff3l.meetings.bot.events.button

import com.w4ff3l.meetings.bot.events.SimpleEventMessageCreator
import com.w4ff3l.meetings.persistence.model.Participant
import com.w4ff3l.meetings.persistence.model.Status
import com.w4ff3l.meetings.persistence.service.MeetingService
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class SimpleEventDecline(
    private val simpleEventMessageCreator: SimpleEventMessageCreator,
    private val meetingService: MeetingService
) :
    SimpleEventButtonInteraction {
    val label = "Decline"

    override fun getCustomId(): ButtonId {
        return ButtonId.DECLINE
    }

    override fun handle(event: ButtonInteractionEvent): Mono<Void> {
        val messageId = event.interaction.message.get().id.asLong()
        val participant = Participant(
            name = event.interaction.user.username,
            discordId = event.interaction.user.id.asLong(),
            status = Status.DECLINED,
            registeredAt = Instant.now()
        )

        return event.edit().withEmbeds(simpleEventMessageCreator.modifyEmbedCreateSpec(event))
            .and(meetingService.saveAndUpdateParticipant(participant, messageId))
    }
}