package com.w4ff3l.meetings.bot.events.model

import discord4j.core.`object`.Embed

class Table(embed: Embed) {
    private val acceptedFieldName = "Accepted"
    private val declinedFieldName = "Declined"

    private val acceptedColumn = mutableSetOf<Person>()
    private val declinedColumn = mutableSetOf<Person>()

    init {
        parseEvent(embed)
    }

    fun addAccepted(id: String) {
        if (isIdInDeclined(id)) {
            declinedColumn.remove(Person(id))
        }
        acceptedColumn.add(Person(id))
    }

    fun addDeclined(id: String) {
        if (isIdInAccepted(id)) {
            acceptedColumn.remove(Person(id))
        }
        declinedColumn.add(Person(id))
    }

    fun stringifyAcceptedColumn(): String {
        return stringifyColumn(acceptedColumn)
    }

    fun stringifyDeclinedColumn(): String {
        return stringifyColumn(declinedColumn)
    }

    private fun isIdInDeclined(id: String): Boolean {
        return declinedColumn.map(Person::id).contains(id)
    }

    private fun isIdInAccepted(id: String): Boolean {
        return acceptedColumn.map(Person::id).contains(id)
    }

    private fun stringifyColumn(people: Set<Person>): String {
        val stringified = people.joinToString("\n", transform = Person::discordReference)

        if (stringified.isBlank()) {
            return "None"
        }

        return stringified
    }

    private fun parseEvent(embed: Embed) {
        val splitEmbedFieldBy: (Iterable<Embed.Field>, String) -> List<String> = { fields, fieldName ->
            fields.first { field -> field.name == fieldName }.value
                .split("\n").filter(isId).map(extractUserId)
        }

        embed.fields.apply {
            splitEmbedFieldBy(this, acceptedFieldName).forEach(this@Table::addAccepted)
            splitEmbedFieldBy(this, declinedFieldName).forEach(this@Table::addDeclined)
        }
    }
}

private val isId: (String) -> Boolean = { s -> !s.contains("none", true) }
private val extractUserId: (String) -> String = { s -> s.substringAfter("@").substringBefore(">") }

