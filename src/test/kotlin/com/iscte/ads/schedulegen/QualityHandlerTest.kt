package com.iscte.ads.schedulegen

import com.iscte.ads.schedulegen.config.QualityParams
import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.schedule.QualityHandler
import com.iscte.ads.schedulegen.schedule.StudentClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class QualityHandlerTest {

    @Test
    fun `GIVEN an overbooking of 10 percent AND a room with 10 percent less capacity WHEN check for match THEN should return true`(){

        val qualityParams = QualityParams(overbookingPercentage = 10, matchForRequiredFeature = false)

        val room = Room(
                building = "Edifício II (ISCTE-IUL)",
                name = "D1.01",
                normalCapacity = 27,
                examCapacity = 21
        )

        val studentClass = StudentClass(
                course = "MEI",
                executionUnit = "Introdução a Aprendizem Automática",
                shift = "night",
                classIdentifier = "MEI-PL-A1",
                subscribersCount = 30,
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                requestedFeature = null
        )
        val qualityHandler = QualityHandler()

        val result = qualityHandler.matchesWithQualityParams(qualityParams, room, studentClass)

        assertEquals(true, result)
    }

    @Test
    fun `GIVEN an overbooking of 0 percent AND a room with 10 percent less capacity WHEN check for match THEN should return false`(){

        val qualityParams = QualityParams(overbookingPercentage = 0, matchForRequiredFeature = false)

        val room = Room(
                building = "Edifício II (ISCTE-IUL)",
                name = "D1.01",
                normalCapacity = 27,
                examCapacity = 21
        )

        val studentClass = StudentClass(
                course = "MEI",
                executionUnit = "Introdução a Aprendizem Automática",
                shift = "night",
                classIdentifier = "MEI-PL-A1",
                subscribersCount = 30,
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                requestedFeature = null
        )
        val qualityHandler = QualityHandler()

        val result = qualityHandler.matchesWithQualityParams(qualityParams, room, studentClass)

        assertEquals(false, result)
    }

    @Test
    fun `GIVEN match feature is required as param AND class has required feature AND room has feature WHEN check for match THEN should return true`() {
        val qualityParams = QualityParams(overbookingPercentage = 10, matchForRequiredFeature = true)

        val room = Room(
                building = "Edifício II (ISCTE-IUL)",
                name = "D1.01",
                normalCapacity = 27,
                examCapacity = 21,
                features = mapOf("movies room" to "X")
        )

        val studentClass = StudentClass(
                course = "MEI",
                executionUnit = "Introdução a Aprendizem Automática",
                shift = "night",
                classIdentifier = "MEI-PL-A1",
                subscribersCount = 30,
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                requestedFeature = "movies room"
        )
        val qualityHandler = QualityHandler()

        val result = qualityHandler.matchesWithQualityParams(qualityParams, room, studentClass)

        assertEquals(true, result)
    }

    @Test
    fun `GIVEN match feature is required as param AND class has required feature AND room does not WHEN check for match THEN should return false`() {
        val qualityParams = QualityParams(overbookingPercentage = 10, matchForRequiredFeature = true)

        val room = Room(
                building = "Edifício II (ISCTE-IUL)",
                name = "D1.01",
                normalCapacity = 27,
                examCapacity = 21,
                features = mapOf("movies room" to "")
        )

        val studentClass = StudentClass(
                course = "MEI",
                executionUnit = "Introdução a Aprendizem Automática",
                shift = "night",
                classIdentifier = "MEI-PL-A1",
                subscribersCount = 30,
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                requestedFeature = "movies room"
        )
        val qualityHandler = QualityHandler()

        val result = qualityHandler.matchesWithQualityParams(qualityParams, room, studentClass)

        assertEquals(false, result)
    }
}