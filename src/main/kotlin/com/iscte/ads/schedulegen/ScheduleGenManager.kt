package com.iscte.ads.schedulegen

interface ScheduleGenManager {
    fun generateSchedule(roomsArray: Array<Room>, classArray: Array<StudentClass>): Schedule
}