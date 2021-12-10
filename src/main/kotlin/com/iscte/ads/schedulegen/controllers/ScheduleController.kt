package com.iscte.ads.schedulegen.controllers

import com.iscte.ads.schedulegen.RoomsGatewayImplementation
import com.iscte.ads.schedulegen.ScheduleGenManagerImplementation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@RestController
class ScheduleController(private val scheduleManager: ScheduleGenManagerImplementation,
                         private val roomsGateway: RoomsGatewayImplementation) {

    @RequestMapping("/schedule")
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