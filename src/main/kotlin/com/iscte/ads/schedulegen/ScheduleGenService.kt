package com.iscte.ads.schedulegen

interface ScheduleGenService {
    fun generateSchedule(rooms: Array<Room>, classes: Array<StudentClass>): Schedule
}