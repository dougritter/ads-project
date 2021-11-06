package com.iscte.ads.schedulegen

class FakeScheduleGenService(val schedule: Schedule): ScheduleGenService {
    override fun generateSchedule(rooms: Array<Room>, classes: Array<StudentClass>) = schedule
}
