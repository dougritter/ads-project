package com.iscte.ads.schedulegen.controllers

import com.iscte.ads.schedulegen.RoomsGatewayImplementation
import com.iscte.ads.schedulegen.ScheduleGenManagerImplementation
import netscape.javascript.JSObject
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class CsvUpload(val rooms: String, val classes: String)

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

    @PostMapping("/upload-csv")
    fun uploadCsv(@RequestBody csvUpload: CsvUpload): String? {
        print("received request to upload-csv")
        print(csvUpload)
        return """
            {
                "response" : "returning something OK to upload-csv endpoint"
            }
        """
    }
}