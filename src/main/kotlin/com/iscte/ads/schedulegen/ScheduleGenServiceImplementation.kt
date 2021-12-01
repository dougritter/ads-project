package com.iscte.ads.schedulegen

import java.time.LocalDate
import java.time.LocalTime
import java.util.NoSuchElementException

class ScheduleGenServiceImplementation(private val slotGenerator: SlotGenerator) : ScheduleGenService {

    override fun generateSchedule(rooms: Array<Room>, classes: Array<StudentClass>): Schedule {

        // The baseline of this algorithm uses two data structures
        // A list of Rooms with their schedules for each day
        // A list of events (unique classes) with their rooms

        // find the first date of class
        // find the last date of class
        // find all days of classes for the schedule
        // fill a list of slots for each day of class with all rooms for each

        // Find days with classes
        //
        val daysOfClasses = mutableListOf<LocalDate>()
        for (studentClass in classes) {
            studentClass.startTime?.apply {
                val uniqueDate = this.toLocalDate()
                if (!daysOfClasses.contains(uniqueDate)) {
                    daysOfClasses.add(uniqueDate)
                }
            }
        }

//        print("ScheduleGenService - found days of all classes: ${daysOfClasses.count()} days for ${classes.count()} classes")

        val allRoomsSchedule = generateRoomsSchedule(rooms, daysOfClasses)
        //        print("ScheduleGenService - generated rooms schedule: $allRoomsSchedule")

        val events = mutableListOf<Event>()

        // for each class, search for a candidate room
        for (studentClass in classes) {
            var candidateRoom: Room? = null
            var candidateStartSlot: Slot? = null
            var candidateEndSlot: Slot? = null

            for (roomSchedule in allRoomsSchedule) {

                val startSlot: Slot
                val endSlot: Slot
                // for each room, search for the corresponding slots
                if (studentClass.startTime != null) {
                    try {
                        val dailySchedule = roomSchedule.days.first { it.day == studentClass.startTime.toLocalDate() }

                        startSlot = dailySchedule.slots.first { it.startTime == studentClass.startTime.toLocalTime() }
                        endSlot = dailySchedule.slots.first { it.endTime == studentClass.endTime!!.toLocalTime() }

                    } catch (exception: NoSuchElementException) {
                        continue
                    }

                    // verify if start slot and end slot are both available
                    val startAndEndSlotsAreAvailable = startSlot.available && endSlot.available

                    // simple logic that currently only checks for room capacity
                    if (startAndEndSlotsAreAvailable) {
                        if (candidateRoom != null) {
                            if (roomSchedule.room.normalCapacity >= studentClass.subscribersCount
                                    && roomSchedule.room.normalCapacity < candidateRoom.normalCapacity) {
                                candidateRoom = roomSchedule.room
                                candidateStartSlot = startSlot
                                candidateEndSlot = endSlot
                            }
                        } else if (roomSchedule.room.normalCapacity >= studentClass.subscribersCount) {
                            candidateRoom = roomSchedule.room
                            candidateStartSlot = startSlot
                            candidateEndSlot = endSlot
                        }
                    }
                }
            }

            candidateRoom?.apply {

                val dayInTheList = allRoomsSchedule.find {
                    it.room.name == candidateRoom.name
                }?.days?.find {
                    it.day == studentClass.startTime?.toLocalDate()
                }

                dayInTheList?.slots?.add(dayInTheList.slots.indexOf(candidateStartSlot), candidateStartSlot?.copy(
                        available = false,
                        classIdentifier = studentClass.classIdentifier)!!)
                dayInTheList?.slots?.add(dayInTheList.slots.indexOf(candidateEndSlot), candidateEndSlot?.copy(
                        available = false,
                        classIdentifier = studentClass.classIdentifier)!!
                )

                events.add(Event(
                        studentClass = studentClass,
                        startTime = studentClass.startTime,
                        endTime = studentClass.endTime,
                        dayOfWeek = studentClass.startTime?.dayOfWeek,
                        room = this
                ))

//                print("ScheduleGenService - added event to schedule: ${events.last()}")
            }
        }

        return Schedule(events = events.toTypedArray())
    }

    private fun generateRoomsSchedule(rooms: Array<Room>, daysOfClasses: MutableList<LocalDate>): MutableList<RoomSchedule> {
        val allRoomsSchedule = mutableListOf<RoomSchedule>()

        for (room in rooms) {
            val roomSchedule = RoomSchedule(room = room, days = mutableListOf())

            for (day in daysOfClasses) {
                roomSchedule.days.add(RoomDay(
                        day = day,
                        roomName = room.name,
                        slots = slotGenerator.generateSlotsForOneDay(
                                startTime = LocalTime.parse("08:00"),
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