package com.iscte.ads.schedulegen.schedule

import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class SlotGeneratorImplementation: SlotGenerator {

    override fun generateSlotsForOneDay(startTime: LocalTime,
                                       numberOfSlots: Int,
                                       slotTime: Int,
                                       roomName: String): MutableList<Slot> {

        var lastEndTime = startTime
        val slots = mutableListOf<Slot>()

        for (index in 1..numberOfSlots) {
            slots.add(Slot(
                    available = true,
                    roomName = roomName,
                    startTime = lastEndTime,
                    endTime = lastEndTime.plusMinutes(slotTime.toLong())
            ))

            lastEndTime = slots.last().endTime
        }

        return slots
    }
}