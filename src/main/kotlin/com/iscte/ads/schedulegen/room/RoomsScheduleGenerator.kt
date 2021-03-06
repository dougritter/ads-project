package com.iscte.ads.schedulegen.room

import java.time.LocalDate

interface RoomsScheduleGenerator {
    fun generateRoomsSchedule(rooms: Array<Room>, daysOfClasses: MutableList<LocalDate>): MutableList<RoomSchedule>
}