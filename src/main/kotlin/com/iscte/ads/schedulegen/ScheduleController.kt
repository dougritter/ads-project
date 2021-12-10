package com.iscte.ads.schedulegen

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@RestController
class ScheduleController(private val scheduleManager: ScheduleGenManagerImplementation,
                         private val roomsGateway: RoomsGatewayImplementation) {

    @RequestMapping("/")
    @ResponseBody
    fun generateSchedule(): String? {
        val dateStart = LocalDateTime.now()
        print("generate schedule requested")
        val result = scheduleManager.generateSchedule(
                roomsGateway.getRoomsList().toTypedArray(),
                roomsGateway.getClassesList().toTypedArray())

        val totalTime = ChronoUnit.MILLIS.between(dateStart, LocalDateTime.now())
        print("schedule created - returning result in $totalTime ms")
        return result.toString()
    }
}