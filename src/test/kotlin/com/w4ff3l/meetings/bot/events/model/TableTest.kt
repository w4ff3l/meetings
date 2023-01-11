package com.w4ff3l.meetings.bot.events.model

import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.Embed
import discord4j.core.spec.EmbedCreateSpec
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class TableTest {
    @MockK
    private lateinit var gdc: GatewayDiscordClient

    @Test
    fun `parses persons correctly upon initialization`() {
        val embed = Embed(gdc, embedDataWithTwoParticipants)
        val table = Table(embed)

        assertEquals(acceptedFieldData, table.stringifyAcceptedColumn())
        assertEquals(declinedFieldData, table.stringifyDeclinedColumn())
    }

    @Test
    fun `stringifies columns correctly`() {
        val embed = Embed(gdc, embedDataWithTwoParticipants)
        val table = Table(embed)

        assertEquals(acceptedFieldData, table.stringifyAcceptedColumn())
        assertEquals(declinedFieldData, table.stringifyDeclinedColumn())
    }

    @Test
    fun `adding person removes 'none' and displays person instead`() {
        val embed = Embed(gdc, embedDatatWithEmptyFields)
        val table = Table(embed)

        table.addAccepted("TestPerson")

        assertEquals(fieldDataWithTestPerson, table.stringifyAcceptedColumn())
    }

    @Test
    fun `adding person a second time does not duplicate person`() {
        val embed = Embed(gdc, embedDatatWithEmptyFields)
        val table = Table(embed)

        table.addAccepted("TestPerson")
        table.addAccepted("TestPerson")

        assertEquals(fieldDataWithTestPerson, table.stringifyAcceptedColumn())
    }

    @Test
    fun `switching column does not duplicate person`() {
        val embed = Embed(gdc, embedDatatWithEmptyFields)
        val table = Table(embed)

        table.addAccepted("TestPerson")
        table.addDeclined("TestPerson")

        assertEquals(fieldDataWithoutPerson, table.stringifyAcceptedColumn())
        assertEquals(fieldDataWithTestPerson, table.stringifyDeclinedColumn())
    }
}

val acceptedFieldData = """
    <@BlaBlubAcc>
    <@BlaBlaAcc>
""".trimIndent()

val declinedFieldData = """
    <@BlaBlubDec>
    <@BlaBlaDec>
""".trimIndent()

var embedDataWithTwoParticipants = EmbedCreateSpec.builder()
    .addField("Accepted", acceptedFieldData, true)
    .addField("Declined", declinedFieldData, true)
    .build().asRequest()

var embedDatatWithEmptyFields = EmbedCreateSpec.builder()
    .addField("Accepted", "None", true)
    .addField("Declined", "None", true)
    .build().asRequest()

const val fieldDataWithoutPerson = "None"
const val fieldDataWithTestPerson = "<@TestPerson>"
