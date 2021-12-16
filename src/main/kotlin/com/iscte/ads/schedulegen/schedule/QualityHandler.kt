package com.iscte.ads.schedulegen.schedule

import com.iscte.ads.schedulegen.config.QualityParams
import com.iscte.ads.schedulegen.room.Room
import org.springframework.stereotype.Service

@Service
class QualityHandler {
    fun matchesWithQualityParams(quality: QualityParams, room: Room, studentClass: StudentClass): Boolean {
        val maxTotalOverbooking = (studentClass.subscribersCount * (quality.overbookingPercentage.toDouble() / 100.0)).toInt()

        // checking for maximum overbooking
        if (room.normalCapacity < studentClass.subscribersCount - maxTotalOverbooking) {
            return false
        }

        // checking for the requested feature
        if (quality.matchForRequiredFeature && studentClass.requestedFeature != null) {
            if (!room.features.containsKey(studentClass.requestedFeature) || room.features[studentClass.requestedFeature]!!.isEmpty()) {
                return false
            }
        }

        return true
    }
}