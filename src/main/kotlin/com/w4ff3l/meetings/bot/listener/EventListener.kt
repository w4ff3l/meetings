package com.w4ff3l.meetings.bot.listener

import discord4j.core.event.domain.Event
import mu.KotlinLogging
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

interface EventListener<T : Event> {
    fun getEventType(): KClass<T>
    fun handle(event: T): Mono<Void>

    fun handleError(): Mono<Void> {
        logger.error { "Unable to process ${getEventType().simpleName}" }
        return Mono.empty()
    }
}