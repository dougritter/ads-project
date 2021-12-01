package com.iscte.ads.schedulegen

import java.time.DayOfWeek
import java.time.LocalDate

class ScheduleGenServiceImplementation: ScheduleGenService {
    override fun generateSchedule(rooms: Array<Room>, classes: Array<StudentClass>): Schedule {
        val roomsMatrix = mutableListOf<MutableList<Room>>()

        val events = mutableListOf<Event>()

        for (studentClass in classes) {
            var candidateRoom: Room? = null

            for (room in roomsMatrix.first()) {
                if (candidateRoom != null) {
                    if (room.normalCapacity >= studentClass.subscribersCount
                            && room.normalCapacity < candidateRoom.normalCapacity) {
                        candidateRoom = room
                    }
                } else if (room.normalCapacity >= studentClass.subscribersCount) {
                    candidateRoom = room
                }
            }

            candidateRoom?.apply {
                events.add(Event(
                        studentClass = studentClass,
                        startTime = LocalDate.now(),
                        endTime = LocalDate.now(),
                        dayOfWeek = LocalDate.now().dayOfWeek,
                        room = this
                ))
            }
        }

        return Schedule(events = events.toTypedArray())
    }
}