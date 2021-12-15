package com.iscte.ads.schedulegen.schedule

import java.time.LocalTime

interface SlotGenerator {
    fun generateSlotsForOneDay(startTime: LocalTime, numberOfSlots: Int, slotTime: Int, roomName: String): MutableList<Slot>
}