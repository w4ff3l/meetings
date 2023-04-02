package com.w4ff3l.meetings.persistence.service

import com.w4ff3l.meetings.persistence.model.Meeting
import com.w4ff3l.meetings.persistence.model.Participant
import com.w4ff3l.meetings.persistence.model.Status
import com.w4ff3l.meetings.persistence.repository.MeetingRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

const val firstMessageId = 1L

@ExtendWith(MockKExtension::class)
class MeetingServiceTest {
    @MockK
    private lateinit var meetingRepository: MeetingRepository

    @InjectMockKs
    private lateinit var meetingService: MeetingService

    @Test
    fun `saves meeting and returns the saved object`() {
        every { meetingRepository.save(meetingToBeSaved) } returns Mono.just(meetingToBeSaved)

        val monoSavedMeeting = meetingService.saveMeeting(meetingToBeSaved)

        StepVerifier.create(monoSavedMeeting)
            .expectNext(meetingToBeSaved)
            .verifyComplete()
        verify { meetingRepository.save(eq(meetingToBeSaved)) }
    }

    @Test
    fun `updates meeting with new participant`() {
        val meetingWithParticipant = meetingToBeSaved.copy(participants = listOf(acceptedParticipant))
        every { meetingRepository.findFirstByMessageId(firstMessageId) } returns Mono.just(meetingToBeSaved)
        every { meetingRepository.save(meetingWithParticipant) } returns Mono.just(meetingWithParticipant)

        val monoSavedMeeting = meetingService.saveAndUpdateParticipant(acceptedParticipant, firstMessageId)

        StepVerifier.create(monoSavedMeeting)
            .expectNext(meetingWithParticipant)
            .verifyComplete()
        verify { meetingRepository.findFirstByMessageId(firstMessageId) }
        verify { meetingRepository.save(meetingWithParticipant) }
    }

    @Test
    fun `updates participant if already present`() {
        val meetingWithParticipant = meetingToBeSaved.copy(participants = listOf(acceptedParticipant))
        val meetingWithDeclinedParticipant = meetingToBeSaved.copy(participants = listOf(declinedParticipant))
        every { meetingRepository.findFirstByMessageId(firstMessageId) } returns Mono.just(meetingWithParticipant)
        every { meetingRepository.save(meetingWithDeclinedParticipant) } returns Mono.just(meetingWithDeclinedParticipant)

        val monoSavedMeeting = meetingService.saveAndUpdateParticipant(declinedParticipant, firstMessageId)

        StepVerifier.create(monoSavedMeeting)
            .expectNext(meetingWithDeclinedParticipant)
            .verifyComplete()
        verify { meetingRepository.findFirstByMessageId(firstMessageId) }
        verify { meetingRepository.save(meetingWithDeclinedParticipant) }
    }
}

val meetingToBeSaved = Meeting(
    "123",
    1L,
    1L,
    "MyMeeting",
    "myCreator",
    Instant.now(),
    Instant.now(),
    emptyList()
)

val acceptedParticipant = Participant(
    name = "AcceptedParticipant",
    discordId = 1L,
    status = Status.ACCEPTED,
    registeredAt = Instant.now()
)

val declinedParticipant = Participant(
    name = "DeclinedParticipant",
    discordId = 1L,
    status = Status.DECLINED,
    registeredAt = Instant.now()
)