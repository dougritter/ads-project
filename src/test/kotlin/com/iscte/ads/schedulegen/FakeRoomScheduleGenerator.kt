package com.iscte.ads.schedulegen

import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.room.RoomSchedule
import com.iscte.ads.schedulegen.room.RoomsScheduleGenerator
import java.time.LocalDate

class FakeRoomScheduleGenerator: RoomsScheduleGenerator {
    var roomsSchedule: MutableList<RoomSchedule>? = null

    override fun generateRoomsSchedule(rooms: Array<Room>, daysOfClasses: MutableList<LocalDate>): MutableList<RoomSchedule> =
        if (roomsSchedule != null) roomsSchedule!! else mutableListOf()
}