package com.iscte.ads.schedulegen

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SchedulegenApplication

fun main(args: Array<String>) {
	runApplication<SchedulegenApplication>(*args)

	val roomsGateway = RoomsGatewayImplementation()
	roomsGateway.loadFile()

	val scheduleService = ScheduleGenServiceImplementation()
	val scheduleManager = ScheduleGenManagerImplementation(scheduleGenService = scheduleService)

	val result = scheduleManager.generateSchedule(roomsGateway.getRoomsList().toTypedArray(), roomsGateway.getClassesList().toTypedArray())
}
