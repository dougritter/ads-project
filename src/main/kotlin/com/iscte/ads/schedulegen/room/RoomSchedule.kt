package com.iscte.ads.schedulegen.room

data class RoomSchedule(
        val room: Room,
        val days: MutableList<RoomDay>
)