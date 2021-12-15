package com.iscte.ads.schedulegen.schedule

import com.iscte.ads.schedulegen.config.QualityParams
import com.iscte.ads.schedulegen.room.Room

interface ScheduleGenManager {
    fun generateSchedule(roomsArray: Array<Room>, classArray: Array<StudentClass>, qualityParams: QualityParams): Schedule
}