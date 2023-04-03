package com.w4ff3l.meetings.persistence.repository

import com.w4ff3l.meetings.persistence.model.Meeting
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.test.StepVerifier
import java.time.Instant

@Testcontainers
@DataMongoTest
class MeetingRepositoryTest {
    @Autowired
    lateinit var meetingRepository: MeetingRepository

    companion object {
        @Container
        var mongoDBContainer: MongoDBContainer = MongoDBContainer("mongo:6.0.4")

        @JvmStatic
        @DynamicPropertySource
        fun properties(dynamicPropertyRegistry: DynamicPropertyRegistry) {
            dynamicPropertyRegistry.add("spring.data.mongodb.host", mongoDBContainer::getHost)
            dynamicPropertyRegistry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort)
        }
    }

    @Test
    fun `save meeting sanity test`() {
        val savedMeetingMono = meetingRepository.save(meetingToSave)
        val actualMeetingFlux = meetingRepository.findAll()

        StepVerifier.create(savedMeetingMono)
            .assertNext {
                assertThat(it).usingRecursiveComparison().ignoringFields("id").isEqualTo(meetingToSave)
            }.verifyComplete()
        StepVerifier.create(actualMeetingFlux)
            .assertNext {
                assertThat(it).usingRecursiveComparison().ignoringFields("id").isEqualTo(meetingToSave)
            }.verifyComplete()
    }

    @Test
    fun `finds meeting by message id`() {
        val savedMeetingMono = meetingRepository.save(meetingToSave)
        val actualMeetingMono = meetingRepository.findFirstByMessageId(meetingToSave.messageId)

        StepVerifier.create(savedMeetingMono)
            .assertNext {
                assertThat(it).usingRecursiveComparison().ignoringFields("id").isEqualTo(meetingToSave)
            }.verifyComplete()
        StepVerifier.create(actualMeetingMono)
            .assertNext {
                assertThat(it).usingRecursiveComparison().ignoringFields("id").isEqualTo(meetingToSave)
            }.verifyComplete()
    }
}

val meetingToSave = Meeting(
    discordServerId = 1L,
    messageId = 1L,
    title = "meeting",
    creator = "creator",
    createdAt = Instant.parse("2023-04-03T20:07:26.396Z"),//2023-04-03T20:07:26.396Z
    startedAt = Instant.parse("2023-04-03T20:07:26.396Z"),
    participants = emptyList()
)
