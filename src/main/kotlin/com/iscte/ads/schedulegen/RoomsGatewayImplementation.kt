package com.iscte.ads.schedulegen

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.FileReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

@Service
class RoomsGatewayImplementation(val objectConverter: ObjectConverter) {
    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

    private lateinit var classesList: MutableList<StudentClass>
    private lateinit var roomsList: MutableList<Room>

    fun getClassesList() = classesList

    fun getRoomsList() = roomsList

    private fun mapToClasses(classesList: List<Map<String, String>>): MutableList<StudentClass> {
        val studentClasses = mutableListOf<StudentClass>()

        for (item in classesList) {
            val subscribersCount = item["subscribersCount"].orEmpty()

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
                    endTime = endTime
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

    fun loadFile() {
        print("load file requested")
        var roomsMap: List<Map<String, String>>?
        var classes: List<Map<String, String>>?

        FileReader("src/main/resources/rooms.csv").use { fileReader ->
            BufferedReader(fileReader).use { reader ->
                val contents = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()))

                val contentsCSV = objectConverter.normalizeRoomsCSV(contents)

                roomsMap = csvReader().readAllWithHeader(contentsCSV)
                roomsList = mapToRooms(roomsMap!!)
                print("finished loading rooms - ${roomsList.size} rooms")
            }
        }

        FileReader("src/main/resources/classes.csv").use { fileReader ->
            BufferedReader(fileReader).use { reader ->
                val contents = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()))

                val contentsCSV = objectConverter.normalizeClassesCSV(contents)

                classes = csvReader().readAllWithHeader(contentsCSV)
                classesList = mapToClasses(classes!!)
                print("finished loading classes - ${classesList.size} classes")
            }
        }
   }

    fun convertToJson(arrayOfEvents: Array<Event>): String {
        return objectConverter.convertToJson(arrayOfEvents)
    }
}