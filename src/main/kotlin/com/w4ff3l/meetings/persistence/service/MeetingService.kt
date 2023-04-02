package com.w4ff3l.meetings.persistence.service

import com.w4ff3l.meetings.persistence.model.Meeting
import com.w4ff3l.meetings.persistence.model.Participant
import com.w4ff3l.meetings.persistence.repository.MeetingRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

private const val MEETING_SAVED = "Meeting saved successfully"
private const val MEETING_SAVED_ERROR = "Meeting could not be saved. Error:"

private const val PARTICIPANT_SAVED = "Participant saved successfully"
private const val PARTICIPANT_SAVED_ERROR = "Participant could not be saved. Error:"

@Service
class MeetingService(val meetingRepository: MeetingRepository) {
    fun saveMeeting(meeting: Meeting): Mono<Meeting> {
        return meetingRepository.save(meeting).doOnSuccess { logger.info(MEETING_SAVED) }
            .doOnError { throwable -> logger.error("$MEETING_SAVED_ERROR ${throwable.message}") }
    }

    fun saveAndUpdateParticipant(participant: Participant, messageId: Long): Mono<Meeting> {
        return meetingRepository.findFirstByMessageId(messageId).flatMap { meeting ->
            val existingParticipantIndex = meeting.participants.indexOfFirst { it.discordId == participant.discordId }
            val updatedParticipants = if (isParticipant(existingParticipantIndex)) {
                meeting.participants.toMutableList().apply {
                    set(existingParticipantIndex, participant)
                }
            } else {
                meeting.participants.plus(participant)
            }

            val updatedMeeting = meeting.copy(participants = updatedParticipants)
            meetingRepository.save(updatedMeeting)
        }.doOnSuccess { logger.info { PARTICIPANT_SAVED } }
            .doOnError { throwable -> logger.error { "$PARTICIPANT_SAVED_ERROR ${throwable.message}" } }
    }
}

fun isParticipant(index: Int): Boolean = index != -1