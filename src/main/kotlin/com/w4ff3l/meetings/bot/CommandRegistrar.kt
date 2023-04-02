package com.w4ff3l.meetings.bot

import com.w4ff3l.meetings.bot.events.command.SlashCommand
import discord4j.core.DiscordClient
import discord4j.discordjson.json.ApplicationCommandData
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class CommandRegistrar(
    @Value("\${discord.applicationId}") private val applicationId: Long,
    discordClient: DiscordClient,
    private val slashCommands: List<SlashCommand>
) {
    private val applicationService = discordClient.applicationService

    fun deleteSlashcommands() {
        logger.info("Deleting old SlashCommands...")
        applicationService.getGlobalApplicationCommands(applicationId).collectMap(ApplicationCommandData::name).block()
            ?.forEach {
                applicationService.deleteGlobalApplicationCommand(
                    applicationId, it.value.id().asLong()
                ).subscribe()
            }
    }

    fun addSlashCommands() {
        logger.info("Adding new SlashCommands...")
        slashCommands.forEach {
            applicationService.createGlobalApplicationCommand(applicationId, it.getCommandRequest())
                .doOnError { throwable -> logger.error { throwable.message } }
                .subscribe {
                    logger.info { "Added SlashCommand ${it.name()}" }
                }
        }
    }
}