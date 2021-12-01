package com.iscte.ads.schedulegen

data class RoomSchedule(
        val room: Room,
        val days: MutableList<RoomDay>
)