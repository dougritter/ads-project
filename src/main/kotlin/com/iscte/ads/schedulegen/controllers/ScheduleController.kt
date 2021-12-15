package com.iscte.ads.schedulegen.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.iscte.ads.schedulegen.room.RoomsGatewayImplementation
import com.iscte.ads.schedulegen.schedule.ScheduleGenManagerImplementation
import org.springframework.web.bind.annotation.*

data class CsvUpload(val rooms: String, val classes: String)

@RestController
class ScheduleController(private val scheduleManager: ScheduleGenManagerImplementation,
                         private val roomsGateway: RoomsGatewayImplementation) {

    @PostMapping("/upload-csv")
    fun uploadCsv(@RequestBody csvUpload: CsvUpload): String? {
        print("received request to upload-csv")

        val mapper = ObjectMapper()
                .registerModule(Jdk8Module())
                .registerModule(JavaTimeModule())
        mapper.findAndRegisterModules()

        val roomsArray = roomsGateway.convertFromRoomsCsv(csvUpload.rooms).toTypedArray()
        val classesArray = roomsGateway.convertFromClassesCsv(csvUpload.classes).toTypedArray()

        val scheduleResult = scheduleManager.generateSchedule(roomsArray, classesArray).events
        val scheduleJson = roomsGateway.convertToJson(scheduleResult)

        return scheduleJson
    }
}