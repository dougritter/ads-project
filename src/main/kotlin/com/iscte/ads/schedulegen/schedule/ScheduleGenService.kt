package com.iscte.ads.schedulegen.schedule

import com.iscte.ads.schedulegen.config.QualityParams
import com.iscte.ads.schedulegen.room.Room

interface ScheduleGenService {
    fun generateSchedule(rooms: Array<Room>, classes: Array<StudentClass>, quality: QualityParams): Schedule
}