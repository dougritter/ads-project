package com.iscte.ads.schedulegen.schedule

import java.time.LocalDateTime

data class StudentClass(
        val course: String,
        val executionUnit: String,
        val shift: String,
        val classIdentifier: String,
        val subscribersCount: Int,
        val startTime: LocalDateTime? = null,
        val endTime: LocalDateTime? = null,
        val requestedFeature: String? = null
)