package com.iscte.ads.schedulegen.room

import com.iscte.ads.schedulegen.schedule.Slot
import java.time.LocalDate

data class RoomDay(
        val day: LocalDate,
        val roomName: String,
        val slots: MutableList<Slot>
)