package com.iscte.ads.schedulegen

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalTime

@SpringBootApplication
class SchedulegenApplication

fun main(args: Array<String>) {
	runApplication<SchedulegenApplication>(*args)

	val roomsGateway = RoomsGatewayImplementation()
	roomsGateway.loadFile()

	val scheduleService = ScheduleGenServiceImplementation(RoomsScheduleGeneratorImplementation(
			dayStartTime = LocalTime.parse("08:00"),
			slotGenerator = SlotGeneratorImplementation()
	))
	val scheduleManager = ScheduleGenManagerImplementation(scheduleGenService = scheduleService)

	val result = scheduleManager.generateSchedule(roomsGateway.getRoomsList().toTypedArray(), roomsGateway.getClassesList().toTypedArray())
	print(result)
}
