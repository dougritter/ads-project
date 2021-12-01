package com.iscte.ads.schedulegen

import java.time.LocalDate
import java.time.LocalTime

class RoomsScheduleGeneratorImplementation(private val dayStartTime: LocalTime,
                                           private val slotGenerator: SlotGenerator) : RoomsScheduleGenerator {

    override fun generateRoomsSchedule(rooms: Array<Room>, daysOfClasses: MutableList<LocalDate>): MutableList<RoomSchedule> {
        val allRoomsSchedule = mutableListOf<RoomSchedule>()

        for (room in rooms) {
            val roomSchedule = RoomSchedule(room = room, days = mutableListOf())

            for (day in daysOfClasses) {
                roomSchedule.days.add(RoomDay(
                        day = day,
                        roomName = room.name,
                        slots = slotGenerator.generateSlotsForOneDay(
                                startTime = dayStartTime,
                                numberOfSlots = 30,
                                slotTime = 30,
                                roomName = room.name
                        )
                ))
            }

            allRoomsSchedule.add(roomSchedule)
        }
        return allRoomsSchedule
    }
}