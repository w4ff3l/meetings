package com.w4ff3l.raid.meetings.bot.events

import com.w4ff3l.raid.meetings.bot.events.button.ButtonId
import com.w4ff3l.raid.meetings.bot.events.model.Table
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.command.ApplicationCommandInteractionOptionValue
import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val ACCEPTED_FIELD_NAME = "Accepted"
private const val DECLINED_FIELD_NAME = "Declined"

@Service
class SimpleEventMessageCreator {
    fun basicEmbedCreateSpec(event: ChatInputInteractionEvent, options: List<String>): EmbedCreateSpec {
        val optionMap = event.options
            .filter { interactionOption -> options.contains(interactionOption.name) }
            .associateBy(ApplicationCommandInteractionOption::getName, ApplicationCommandInteractionOption::getValue)

        val title: String = optionMap["title"]!!.orElseThrow().asString()
        val dateVal: ApplicationCommandInteractionOptionValue = optionMap["date"]!!.orElseThrow()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val date = LocalDate.parse(dateVal.asString(), formatter)
        val author: String = event.interaction.user.username

        return EmbedCreateSpec.builder().title("$date - $title")
            .author(EmbedCreateFields.Author.of("Event created by $author", "", ""))
            .addField(ACCEPTED_FIELD_NAME, "None", true)
            .addField(DECLINED_FIELD_NAME, "None", true).build()
    }

    fun modifyEmbedCreateSpec(event: ButtonInteractionEvent): EmbedCreateSpec {
        val currentEmbed = event.interaction.message.orElseThrow().embeds[0]
        val title = currentEmbed.title.orElseThrow()
        val author = currentEmbed.author.orElseThrow().name.orElseThrow()
        val userId = event.interaction.user.id.asString()
        val table = Table(currentEmbed)

        when (event.customId) {
            ButtonId.ACCEPT.id -> table.addAccepted(userId)
            ButtonId.DECLINE.id -> table.addDeclined(userId)
            else -> throw ButtonIdNotFoundException()
        }

        return EmbedCreateSpec.builder()
            .title(title)
            .author(author, "", "")
            .addField(ACCEPTED_FIELD_NAME, table.stringifyAcceptedColumn(), true)
            .addField(DECLINED_FIELD_NAME, table.stringifyDeclinedColumn(), true)
            .build()
    }
}

class ButtonIdNotFoundException : Throwable()
