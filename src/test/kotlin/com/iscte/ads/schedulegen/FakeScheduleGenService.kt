package com.iscte.ads.schedulegen

import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.schedule.Schedule
import com.iscte.ads.schedulegen.schedule.ScheduleGenService
import com.iscte.ads.schedulegen.schedule.StudentClass

class FakeScheduleGenService(val schedule: Schedule): ScheduleGenService {
    override fun generateSchedule(rooms: Array<Room>, classes: Array<StudentClass>) = schedule
}
