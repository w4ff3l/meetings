package com.w4ff3l.meetings.bot

import com.w4ff3l.meetings.bot.listener.EventListener
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import mu.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class Bot<T : Event>(
    private val discordClient: DiscordClient,
    private val eventListeners: List<EventListener<T>>,
    private val commandRegistrar: CommandRegistrar
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        // The order is important since we block with the registration of the events.
        // Always keep the blocking operation as the last step.
//        registerCommands()
        registerEvents()
    }

    private fun registerCommands() {
        discordClient.guilds.subscribe {
            commandRegistrar.deleteSlashcommands()
            commandRegistrar.addSlashCommands()
        }
    }

    private fun registerEvents() {
        val events = mutableListOf<Mono<Void>>()

        logger.info { "Creating Client..." }
        discordClient.withGateway { gatewayDiscordClient ->
            logger.info { "Adding EventListeners. Found ${eventListeners.size}" }
            events.addEvents(gatewayDiscordClient, eventListeners)

            Mono.`when`(events)
        }.block()
    }
}

private fun <T : Event> MutableList<Mono<Void>>.addEvents(
    gatewayDiscordClient: GatewayDiscordClient, eventListeners: List<EventListener<T>>
) {
    eventListeners.forEach {
        this.add(
            gatewayDiscordClient.on(it.getEventType().java, it::handle).then()
        )
        logger.info { "Added Listener for Event ${it.getEventType().simpleName}" }
    }
}
