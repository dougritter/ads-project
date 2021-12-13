package com.iscte.ads.schedulegen.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.iscte.ads.schedulegen.RoomsGatewayImplementation
import com.iscte.ads.schedulegen.ScheduleGenManagerImplementation
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

        return roomsGateway.convertToJson(scheduleManager.generateSchedule(
                roomsGateway.convertFromRoomsCsv(csvUpload.rooms).toTypedArray(),
                roomsGateway.convertFromClassesCsv(csvUpload.classes).toTypedArray())
                .events)
    }
}