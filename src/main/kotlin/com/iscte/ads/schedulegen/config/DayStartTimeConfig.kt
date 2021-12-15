package com.iscte.ads.schedulegen.config

import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class DayStartTimeConfig {
    fun firstTimeOfTheDay(): LocalTime = LocalTime.parse("08:00")
}