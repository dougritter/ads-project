package com.iscte.ads.schedulegen

import java.time.LocalDate

class FakeRoomScheduleGenerator: RoomsScheduleGenerator {
    var roomsSchedule: MutableList<RoomSchedule>? = null

    override fun generateRoomsSchedule(rooms: Array<Room>, daysOfClasses: MutableList<LocalDate>): MutableList<RoomSchedule> =
        if (roomsSchedule != null) roomsSchedule!! else mutableListOf()
}