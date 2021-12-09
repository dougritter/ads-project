package com.iscte.ads.schedulegen

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalTime

@Controller
@SpringBootApplication
class SchedulegenApplication {

	private val roomsGateway = RoomsGatewayImplementation()
	private val scheduleService = ScheduleGenServiceImplementation(RoomsScheduleGeneratorImplementation(
			dayStartTime = LocalTime.parse("08:00"),
			slotGenerator = SlotGeneratorImplementation()
	))
	private val scheduleManager = ScheduleGenManagerImplementation(scheduleGenService = scheduleService)

	@RequestMapping("/")
	@ResponseBody
	fun generateSchedule(): String? {
		roomsGateway.loadFile()
		val result = scheduleManager.generateSchedule(roomsGateway.getRoomsList().toTypedArray(), roomsGateway.getClassesList().toTypedArray())
		print(result)
		return result.toString()
	}

}

fun main(args: Array<String>) {
	runApplication<SchedulegenApplication>(*args)

}
