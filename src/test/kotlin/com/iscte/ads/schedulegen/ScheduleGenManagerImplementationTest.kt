package com.iscte.ads.schedulegen

import com.iscte.ads.schedulegen.config.QualityParams
import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.schedule.Event
import com.iscte.ads.schedulegen.schedule.Schedule
import com.iscte.ads.schedulegen.schedule.ScheduleGenManagerImplementation
import com.iscte.ads.schedulegen.schedule.StudentClass
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScheduleGenManagerImplementationTest {

    @Test
    fun `GIVEN a set up manager WHEN generate schedule is called THEN should return a schedule`() {
        val startDate = LocalDateTime.now()
        val endDate = LocalDateTime.now()

        val classList = arrayOf(
                StudentClass(
                        course = "MEI",
                        executionUnit = "Arquitetura e Desenho de Software",
                        shift = "night",
                        classIdentifier = "MEI-PL-A1",
                        subscribersCount = 30,
                        startTime = LocalDateTime.now(),
                        endTime = LocalDateTime.now(),
                        requestedFeature = null
                ),
                StudentClass(
                        course = "MEI",
                        executionUnit = "Introdução a Aprendizem Automática",
                        shift = "night",
                        classIdentifier = "MEI-PL-A1",
                        subscribersCount = 29,
                        startTime = LocalDateTime.now(),
                        endTime = LocalDateTime.now(),
                        requestedFeature = null
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

        val expectedResult = Schedule(arrayOf(
                Event(
                        studentClass = classList[0],
                        startTime = startDate,
                        endTime = endDate,
                        dayOfWeek = startDate.dayOfWeek,
                        room = roomList[1]
                ),
                Event(
                        studentClass = classList[1],
                        startTime = startDate,
                        endTime = endDate,
                        dayOfWeek = startDate.dayOfWeek,
                        room = roomList[0]
                )
        ))

        val fakeScheduleGenService = FakeScheduleGenService(expectedResult)
        val manager = ScheduleGenManagerImplementation(scheduleGenService = fakeScheduleGenService)

        val result: Schedule = manager.generateSchedule(roomList, classList, QualityParams(10, true))

        assertEquals(expectedResult, result)
    }
}