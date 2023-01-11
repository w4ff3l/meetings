package com.w4ff3l.meetings.bot.events.command

import com.w4ff3l.meetings.bot.events.button.SimpleEventAccept
import com.w4ff3l.meetings.bot.events.button.SimpleEventDecline
import com.w4ff3l.meetings.bot.events.SimpleEventMessageCreator
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import discord4j.core.`object`.entity.Message
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.json.ImmutableApplicationCommandRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SimpleCommand(
    private val simpleEventMessageCreator: SimpleEventMessageCreator,
    private val simpleEventAccept: SimpleEventAccept,
    private val simpleEventDecline: SimpleEventDecline
) : SlashCommand {
    private val commandName: String = "sevent"
    private val dateOption: String = "date"
    private val titleOption: String = "title"

    override fun getName(): String {
        return commandName
    }

    override fun getCommandRequest(): ImmutableApplicationCommandRequest {
        return ApplicationCommandRequest.builder()
            .name(commandName)
            .description("Create an event with different options")
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(dateOption)
                    .description("[String] - Date of Format: YYYYMMDD")
                    .type(ApplicationCommandOption.Type.STRING.getValue())
                    .required(true)
                    .build()
            )
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(titleOption)
                    .description("[String] - Title of the Event")
                    .type(ApplicationCommandOption.Type.STRING.value)
                    .required(true).build()
            )
            .build()
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return reply(event).and(createMessage(event))
    }

    private fun reply(event: ChatInputInteractionEvent): Mono<Void> {
        return event.reply()
            .withEphemeral(true)
            .withContent("Event created :)")
            .log()
    }

    private fun createMessage(event: ChatInputInteractionEvent): Mono<Message> {
        return event.interaction.channel
            .flatMap { messageChannel ->
                messageChannel
                    .createMessage(simpleEventMessageCreator.basicEmbedCreateSpec(event, this.getOptions()))
                    .withComponents(
                        ActionRow.of(
                            Button.success(simpleEventAccept.getCustomId().id, simpleEventAccept.label),
                            Button.danger(simpleEventDecline.getCustomId().id, simpleEventDecline.label)
                        )
                    )
            }
    }

    private fun getOptions(): List<String> {
        return listOf(dateOption, titleOption)
    }
}