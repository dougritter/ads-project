package com.iscte.ads.schedulegen

import com.iscte.ads.schedulegen.config.DayStartTimeConfig
import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.room.RoomsScheduleGeneratorImplementation
import com.iscte.ads.schedulegen.schedule.Slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class RoomsScheduleGeneratorImplementationTest {

    @Test
    fun `GIVEN a rooms array AND a list of days WHEN generate rooms schedule is called THEN should return a list of room schedule`() {

        val fakeSlotGenerator = FakeSlotGenerator()
        fakeSlotGenerator.slots = mutableListOf(
                Slot( available = true,
                        roomName = "fake-room",
                        startTime = LocalTime.parse("08:00"),
                        endTime = LocalTime.parse("08:30")
                ),
                Slot( available = true,
                        roomName = "fake-room",
                        startTime = LocalTime.parse("08:30"),
                        endTime = LocalTime.parse("09:00")
                ),
                Slot( available = true,
                        roomName = "fake-room",
                        startTime = LocalTime.parse("09:00"),
                        endTime = LocalTime.parse("09:30")
                ),
                Slot( available = true,
                        roomName = "fake-room",
                        startTime = LocalTime.parse("09:30"),
                        endTime = LocalTime.parse("10:00")
                ),
        )

        val roomsScheduleGenerator = RoomsScheduleGeneratorImplementation(
                dayStartTime = DayStartTimeConfig(),
                slotGenerator = fakeSlotGenerator
        )

        val arrayOfRooms = arrayOf(
                Room(
                        building = "Edifício II (ISCTE-IUL)",
                        name = "B1",
                        normalCapacity = 29,
                        examCapacity = 0
                ),
                Room(
                        building = "Edifício II (ISCTE-IUL)",
                        name = "D1.01",
                        normalCapacity = 35,
                        examCapacity = 21
                )
        )

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val arrayOfDays = mutableListOf<LocalDate>(
                LocalDate.parse("09-12-2021", formatter),
                LocalDate.parse("19-12-2021", formatter)
        )

        val result = roomsScheduleGenerator.generateRoomsSchedule(rooms = arrayOfRooms, daysOfClasses = arrayOfDays)

        assertEquals(2, result[0].days.size)
        assertEquals(LocalDate.parse("09-12-2021", formatter), result[0].days[0].day)
        assertEquals(LocalDate.parse("19-12-2021", formatter), result[0].days[1].day)
        assertEquals(fakeSlotGenerator.slots, result[0].days[0].slots)

    }
}