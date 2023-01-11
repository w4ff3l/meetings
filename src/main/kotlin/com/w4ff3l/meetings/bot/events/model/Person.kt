package com.w4ff3l.meetings.bot.events.model

data class Person(val id: String) {
    fun discordReference(): String {
        return "<@${id}>"
    }
}
