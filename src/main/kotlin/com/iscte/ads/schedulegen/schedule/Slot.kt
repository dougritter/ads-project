package com.iscte.ads.schedulegen.schedule

import java.time.LocalTime

data class TimeSlot(val day: String,
                    val time: String,
                    val period: Int,
                    val available: Boolean = true)

//Dia; Hora; Periodo
//Seg; 8:00; 1
//Seg; 8:30; 1

data class Slot(
        val available: Boolean,
        val roomName: String,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val classIdentifier:  String? = null
)