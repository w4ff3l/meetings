package com.w4ff3l.raid.meetings.bot

import com.w4ff3l.raid.meetings.bot.events.command.SlashCommand
import discord4j.core.DiscordClient
import discord4j.discordjson.json.ApplicationCommandData
import discord4j.rest.service.ApplicationService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
@Order(1)
class CommandRegistrar(
    @Value("\${guild-id}") val guildId: Long,
    private val discordClient: DiscordClient,
    private val slashCommands: List<SlashCommand>
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        logger.info("Updating SlashCommands")
        val applicationService = discordClient.applicationService

        discordClient.applicationId.block()
            ?.let {
                deleteSlashcommands(guildId, applicationService, it)
                addSlashCommands(slashCommands, guildId, discordClient.applicationService, it)
            }
        logger.info("SlashCommands updated")
    }

    fun addSlashCommands(
        slashCommands: List<SlashCommand>,
        guildId: Long,
        applicationService: ApplicationService,
        applicationId: Long
    ) {
        logger.info("Adding new SlashCommands...")
        slashCommands.forEach {
            applicationService.createGuildApplicationCommand(applicationId, guildId, it.getCommandRequest())
                .doOnEach { logger.info { "Added SlashCommand ${it.get()?.name()}" } }
                .doOnError { throwable -> logger.error { throwable.message } }
                .block()
        }
    }

    fun deleteSlashcommands(guildId: Long, applicationService: ApplicationService, applicationId: Long) {
        logger.info("Deleting old SlashCommands...")
        applicationService.getGuildApplicationCommands(applicationId, guildId)
            .collectMap(ApplicationCommandData::name)
            .block()?.forEach {
                applicationService.deleteGuildApplicationCommand(applicationId, guildId, it.value.id().asLong()).block()
            }
    }
}
