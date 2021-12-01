package com.iscte.ads.schedulegen

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

data class Schedule(
        val events: Array<Event>
)

data class Event(
        val studentClass: StudentClass,
        val startTime: LocalDateTime?,
        val endTime: LocalDateTime?,
        val dayOfWeek: DayOfWeek?,
        val room: Room? = null
)