package com.iscte.ads.schedulegen

class ScheduleGenManagerImplementation(val scheduleGenService: ScheduleGenService): ScheduleGenManager {
    override fun generateSchedule(roomsArray: Array<Room>, classArray: Array<StudentClass>): Schedule {
        return scheduleGenService.generateSchedule(roomsArray, classArray)
    }
}