package com.w4ff3l.raid.meetings.bot.listener

import com.w4ff3l.raid.meetings.bot.events.button.SimpleEventButtonInteraction
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class ButtonInteractionListenerTest {
    @MockK
    private lateinit var buttonInteractionEvent: ButtonInteractionEvent
    @MockK
    private lateinit var simpleEventButtonInteraction1: SimpleEventButtonInteraction
    @MockK
    private lateinit var simpleEventButtonInteraction2: SimpleEventButtonInteraction

    private lateinit var interactionListener: ButtonInteractionListener

    @BeforeEach
    fun setUp() {
        interactionListener =
            ButtonInteractionListener(listOf(simpleEventButtonInteraction1, simpleEventButtonInteraction2))
    }

    @Test
    fun `getEventType sanity test`() {
        assertEquals(ButtonInteractionListener::class, interactionListener::class)
    }

    @Test
    fun `returns correct sub-handle of interactionEvent`() {
        every { simpleEventButtonInteraction1.getCustomId().id } returns "testId1"
        every { simpleEventButtonInteraction2.getCustomId().id } returns "testId2"
        every { buttonInteractionEvent.customId } returns "testId1"

        interactionListener.handle(buttonInteractionEvent).subscribe()

        verify { simpleEventButtonInteraction1.handle(any()) }
    }
}