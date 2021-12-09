package com.iscte.ads.schedulegen

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ScheduleGenServiceImplementationTest {

    @Test
    fun `GIVEN a service WHEN generate schedule is called THEN should return a schedule`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now()

        val classList = arrayOf(
                StudentClass(
                        course = "MEI",
                        executionUnit = "Arquitetura e Desenho de Software",
                        shift = "night",
                        classIdentifier = "MEI-PL-A1",
                        subscribersCount = 30,
                        startTime = LocalDateTime.now(),
                        endTime = LocalDateTime.now()
                ),
                StudentClass(
                        course = "MEI",
                        executionUnit = "Introdução a Aprendizem Automática",
                        shift = "night",
                        classIdentifier = "MEI-PL-A1",
                        subscribersCount = 29,
                        startTime = LocalDateTime.now(),
                        endTime = LocalDateTime.now()
                )
        )

        val roomList = arrayOf(
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

        val service = ScheduleGenServiceImplementation(FakeRoomScheduleGenerator())

        val result = service.generateSchedule(rooms = roomList, classes = classList)
    }

}