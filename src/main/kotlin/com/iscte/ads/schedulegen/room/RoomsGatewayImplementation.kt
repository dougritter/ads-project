package com.iscte.ads.schedulegen.room

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.iscte.ads.schedulegen.schedule.Event
import com.iscte.ads.schedulegen.datamapping.ObjectConverter
import com.iscte.ads.schedulegen.schedule.StudentClass
import com.iscte.ads.schedulegen.schedule.TimeSlot
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class RoomsGatewayImplementation(val objectConverter: ObjectConverter) {
    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

    private fun mapToClasses(classesList: List<Map<String, String>>): MutableList<StudentClass> {
        val studentClasses = mutableListOf<StudentClass>()

        for (item in classesList) {
            val subscribersCount = item["subscribersCount"].orEmpty()
            val requestedFeature = item["Características da sala pedida para a aula"].orEmpty()
            val slots = item["slots"].orEmpty()

            var startTime: LocalDateTime? = null
            var endTime: LocalDateTime? = null
            val day = item["Dia"].orEmpty().replace("/", "-")

            if (day.isNotEmpty()) {
                val startTimeString = "$day ${item["startTime"]}"
                val endTimeString = "$day ${item["endTime"]}"

                startTime = LocalDateTime.parse(startTimeString, formatter)
                endTime = LocalDateTime.parse(endTimeString, formatter)
            }

            studentClasses.add(StudentClass(
                    course = item["course"].orEmpty(),
                    executionUnit = item["executionUnit"].orEmpty(),
                    shift = item["shift"].orEmpty(),
                    classIdentifier = item["classIdentifier"].orEmpty(),
                    subscribersCount =  if (subscribersCount.isNotEmpty()) subscribersCount.toInt() else 0,
                    startTime = startTime,
                    endTime = endTime,
                    requestedFeature = requestedFeature.ifEmpty { null },
                    slots = slots.toInt()
            ))
        }

        return studentClasses
    }

    private fun mapToRooms(roomsList: List<Map<String, String>>): MutableList<Room> {
        val rooms = mutableListOf<Room>()

        for (item in roomsList) {
            val roomMap = item.toMutableMap()
            val building = item["building"].orEmpty()
            val name = item["name"].orEmpty()
            val normalCapatity = item["normalCapacity"].orEmpty()
            val examCapatity = item["examCapacity"].orEmpty()

            roomMap.remove("building")
            roomMap.remove("name")
            roomMap.remove("normalCapacity")
            roomMap.remove("examCapacity")

            rooms.add(Room(
                    building = building,
                    name = name,
                    normalCapacity = if (normalCapatity.isNotEmpty()) normalCapatity.toInt() else 0,
                    examCapacity =  if (examCapatity.isNotEmpty()) examCapatity.toInt() else 0,
                    features = roomMap
            ))
        }

        return rooms
    }

    private fun mapToTimeSlots(timeSlotsLit: List<Map<String, String>>): MutableList<TimeSlot> {
        val timeSlots = mutableListOf<TimeSlot>()

        timeSlotsLit.forEach {
            timeSlots.add(TimeSlot(day = it["day"].orEmpty(),
                time = it["time"].orEmpty(),
                period = it["period"]?.toInt() ?: -1))
        }

        return timeSlots
    }

    fun convertFromRoomsCsv(roomsCsv: String): MutableList<Room> {
        val contentsCSV = objectConverter.normalizeRoomsCSV(roomsCsv)
        val roomsMap = csvReader().readAllWithHeader(contentsCSV)
        return mapToRooms(roomsMap)
    }

    fun convertFromClassesCsv(classesCsv: String): MutableList<StudentClass> {
        val contentsCSV = objectConverter.normalizeClassesCSV(classesCsv)
        val classes = csvReader().readAllWithHeader(contentsCSV)
        return mapToClasses(classes)
    }

    fun convertFromTimeSlotsCsv(timeSlots: String): MutableList<TimeSlot> {
        val contentsCSV = objectConverter.normalizeTimeSlotsCSV(timeSlots)
        val timeSlotsList = csvReader().readAllWithHeader(contentsCSV)
        return mapToTimeSlots(timeSlotsList)
    }

    fun convertToJson(arrayOfEvents: Array<Event>): String {
        return objectConverter.convertToJson(arrayOfEvents)
    }
}