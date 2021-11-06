package com.iscte.ads.schedulegen

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SchedulegenApplication

fun main(args: Array<String>) {
	runApplication<SchedulegenApplication>(*args)
}
