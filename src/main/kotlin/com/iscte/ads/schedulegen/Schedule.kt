package com.iscte.ads.schedulegen

import java.time.DayOfWeek
import java.time.LocalDateTime

data class Schedule(
        val events: Array<Event>
)