package com.iscte.ads.schedulegen

import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RoomsScheduleGeneratorImplementation(private val dayStartTime: DayStartTimeConfig,
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
                                startTime = dayStartTime.firstTimeOfTheDay(),
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