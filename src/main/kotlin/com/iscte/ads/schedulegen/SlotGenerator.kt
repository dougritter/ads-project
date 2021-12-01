package com.iscte.ads.schedulegen

import java.time.LocalTime

interface SlotGenerator {
    fun generateSlotsForOneDay(startTime: LocalTime, numberOfSlots: Int, slotTime: Int, roomName: String): MutableList<Slot>
}