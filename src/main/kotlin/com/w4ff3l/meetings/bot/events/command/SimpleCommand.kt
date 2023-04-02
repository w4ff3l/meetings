package com.w4ff3l.meetings.bot.events.command

import com.w4ff3l.meetings.bot.events.SimpleEventMessageCreator
import com.w4ff3l.meetings.bot.events.button.SimpleEventAccept
import com.w4ff3l.meetings.bot.events.button.SimpleEventDecline
import com.w4ff3l.meetings.persistence.model.Meeting
import com.w4ff3l.meetings.persistence.service.MeetingService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import discord4j.core.`object`.entity.Message
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.json.ImmutableApplicationCommandRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private const val EVENT_CREATED_MESSAGE = "event created :)"

private const val TITLE_DESCRIPTION = "[String] - Title of the Event"
private const val DATE_DESCRIPTION = "[String] - Date of Format: YYYYMMDD"

private const val TITLE_OPTION = "title"
private const val DATE_OPTION = "date"

@Component
class SimpleCommand(
    private val simpleEventMessageCreator: SimpleEventMessageCreator,
    private val simpleEventAccept: SimpleEventAccept,
    private val simpleEventDecline: SimpleEventDecline,
    private val meetingService: MeetingService
) : SlashCommand {
    private val commandName: String = "sevent"

    override fun getName(): String {
        return commandName
    }

    override fun getCommandRequest(): ImmutableApplicationCommandRequest {
        return ApplicationCommandRequest.builder()
            .name(commandName)
            .description("Create an event with different options")
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(DATE_OPTION)
                    .description(DATE_DESCRIPTION)
                    .type(ApplicationCommandOption.Type.STRING.getValue())
                    .required(true)
                    .build()
            )
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(TITLE_OPTION)
                    .description(TITLE_DESCRIPTION)
                    .type(ApplicationCommandOption.Type.STRING.value)
                    .required(true).build()
            )
            .build()
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return createMessage(event).flatMap{ persistMeeting(event, it).then(reply(event)) }
    }

    private fun reply(event: ChatInputInteractionEvent): Mono<Void> {
        return event.reply()
            .withEphemeral(true)
            .withContent(EVENT_CREATED_MESSAGE)
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

    private fun persistMeeting(event: ChatInputInteractionEvent, message: Message): Mono<Meeting> {
        val optionMap = event.options
            .filter { interactionOption ->
                this.getOptions().contains(interactionOption.name)
            }
            .associateBy(ApplicationCommandInteractionOption::getName, ApplicationCommandInteractionOption::getValue)
        val title: String = optionMap[TITLE_OPTION]!!.orElseThrow().asString()

        val meeting = Meeting(
            discordServerId = event.interaction.guildId.get().asLong(),
            messageId = message.id.asLong(),
            title = title,
            creator = event.interaction.user.id.asString(),
            createdAt = event.interaction.id.timestamp,
            participants = emptyList()
        )

        return meetingService.saveMeeting(meeting)
    }

    private fun getOptions(): List<String> {
        return listOf(DATE_OPTION, TITLE_OPTION)
    }
}