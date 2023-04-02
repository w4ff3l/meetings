package com.w4ff3l.meetings.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.time.Instant

@Document(collection = "Meetings")
data class Meeting(
    @Id
    val id: String? = null,
    val discordServerId: Long,
    val messageId: Long,
    val title: String,
    val creator: String,
    val createdAt: Instant,
    val startedAt: Instant? = null,
    val participants: Collection<Participant>
)

data class Participant(
    val name: String,
    val discordId: Long,
    @Field(targetType = FieldType.STRING)
    val status: Status,
    val registeredAt: Instant
)

enum class Status {
    ACCEPTED, DECLINED
}