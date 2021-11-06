package com.iscte.ads.schedulegen

import java.time.DayOfWeek
import java.time.LocalDate

data class Schedule(
        val events: Array<Event>
)

data class Event(
        val studentClass: StudentClass,
        val startTime: LocalDate,
        val endTime: LocalDate,
        val dayOfWeek: DayOfWeek,
        val room: Room
)