package com.iscte.ads.schedulegen

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalTime

class SlotGeneratorImplementationTest {

    @Test
    fun `GIVEN a start time WHEN generate slots is requested THEN should return a list of slots`() {
        val slotGenerator = SlotGeneratorImplementation()

        val expectedResult = mutableListOf(
                Slot(
                        available = true,
                        roomName = "a-room",
                        startTime = LocalTime.parse("08:00"),
                        endTime = LocalTime.parse("08:30"),
                ),
                Slot(
                        available = true,
                        roomName = "a-room",
                        startTime = LocalTime.parse("08:30"),
                        endTime = LocalTime.parse("09:00"),
                )
        )

        val result = slotGenerator.generateSlotsForOneDay(
                startTime = LocalTime.parse("08:00"),
                numberOfSlots = 2,
                slotTime = 30,
                roomName = "a-room"
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN a start time WHEN generate slots is requested for 30 slots THEN should return a list of slots for that day`() {
        val slotGenerator = SlotGeneratorImplementation()

        val firstSlot = Slot(
                available = true,
                roomName = "a-room",
                startTime = LocalTime.parse("08:00"),
                endTime = LocalTime.parse("08:30"),
        )
        val lastSlot = Slot(
                available = true,
                roomName = "a-room",
                startTime = LocalTime.parse("22:30"),
                endTime = LocalTime.parse("23:00"),
        )

        val result = slotGenerator.generateSlotsForOneDay(
                startTime = LocalTime.parse("08:00"),
                numberOfSlots = 30,
                slotTime = 30,
                roomName = "a-room"
        )

        assertEquals(firstSlot, result.first())
        assertEquals(lastSlot, result.last())
    }
}