package com.iscte.ads.schedulegen.schedule

import java.time.LocalTime

data class Slot(
        val available: Boolean,
        val roomName: String,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val classIdentifier:  String? = null
)