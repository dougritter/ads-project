package com.iscte.ads.schedulegen.schedule

import com.iscte.ads.schedulegen.config.QualityParams
import com.iscte.ads.schedulegen.room.Room
import org.springframework.stereotype.Service

@Service
class ScheduleGenManagerImplementation(val scheduleGenService: ScheduleGenService): ScheduleGenManager {
    override fun generateSchedule(roomsArray: Array<Room>, classArray: Array<StudentClass>, qualityParams: QualityParams): Schedule {
        return scheduleGenService.generateSchedule(roomsArray, classArray, qualityParams)
    }
}