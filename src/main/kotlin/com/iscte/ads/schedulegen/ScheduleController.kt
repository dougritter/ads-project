package com.iscte.ads.schedulegen

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ScheduleController(private val scheduleManager: ScheduleGenManagerImplementation,
                         private val roomsGateway: RoomsGatewayImplementation) {

    @RequestMapping("/")
    @ResponseBody
    fun generateSchedule(): String? {
        val result = scheduleManager.generateSchedule(
                roomsGateway.getRoomsList().toTypedArray(),
                roomsGateway.getClassesList().toTypedArray())
        print("schedule created - returning result")
        return result.toString()
    }
}