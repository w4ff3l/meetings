package com.w4ff3l.meetings.persistence.repository

import com.w4ff3l.meetings.persistence.model.Meeting
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface MeetingRepository : ReactiveMongoRepository<Meeting, String> {
    fun findFirstByMessageId(messageId: Long): Mono<Meeting>
}