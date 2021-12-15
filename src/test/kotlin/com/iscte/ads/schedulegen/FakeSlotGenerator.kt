package com.iscte.ads.schedulegen

import com.iscte.ads.schedulegen.schedule.Slot
import com.iscte.ads.schedulegen.schedule.SlotGenerator
import java.time.LocalTime

class FakeSlotGenerator: SlotGenerator {
    var slots: MutableList<Slot>? = null

    override fun generateSlotsForOneDay(startTime: LocalTime,
                                        numberOfSlots: Int,
                                        slotTime: Int,
                                        roomName: String): MutableList<Slot> =
        if (slots != null) slots!! else mutableListOf()
}