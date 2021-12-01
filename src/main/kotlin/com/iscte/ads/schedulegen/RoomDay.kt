package com.iscte.ads.schedulegen

import java.time.LocalDate

data class RoomDay(
        val day: LocalDate,
        val roomName: String,
        val slots: MutableList<Slot>
)