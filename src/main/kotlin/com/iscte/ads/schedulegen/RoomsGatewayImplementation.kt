package com.iscte.ads.schedulegen

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.BufferedReader
import java.io.FileReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors


class RoomsGatewayImplementation {
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

    private fun normalizeClassesCSV(csv: String): String {
        var result = csv
        result = result.replaceFirst("Curso", "course")
        result = result.replaceFirst("Unidade de execução", "executionUnit")
        result = result.replaceFirst("Turno", "shift")
        result = result.replaceFirst("Turma", "classIdentifier")
        result = result.replaceFirst("Inscritos no turno (no 1º semestre é baseado em estimativas)",
                "subscribersCount")

        result = result.replaceFirst("Início", "startTime")
        result = result.replaceFirst("Fim", "endTime")
        result = result.replace(",", "-")
        result = result.replace(";", ",")

        return result
    }

    private fun normalizeRoomsCSV(csv: String): String {
        var result = csv
        result = result.replaceFirst("Edificio", "building")
        result = result.replaceFirst("Nome sala", "name")
        result = result.replaceFirst("Capacidade Normal", "normalCapacity")
        result = result.replaceFirst("Capacidade Exame", "examCapacity")
        result = result.replace(";", ",")

        // Edificio,Nome sala,Capacidade Normal,Capacidade Exame,Nº caracteristicas,Anfiteatro aulas,Apoio tecnico eventos,
        // Arq 1,Arq 2,Arq 3,Arq 4,Arq 5,Arq 6,Arq 9,BYOD (Bring Your Own Device),
        // Focus Group,Horário sala visível portal público,Laboratório de Arquitectura de Computadores I,
        // Laboratório de Arquitectura de Computadores II,Laboratório de Bases de Engenharia,Laboratório de Electrónica,
        // Laboratório de Informática,Laboratório de Jornalismo,Laboratório de Redes de Computadores I,
        // Laboratório de Redes de Computadores II,Laboratório de Telecomunicações,Sala Aulas Mestrado,Sala Aulas Mestrado Plus,
        // Sala NEE,Sala Provas,Sala Reunião,Sala de Arquitectura,Sala de Aulas normal,videoconferencia,Átrio

        return result
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

    fun loadFile() {
        var roomsMap: List<Map<String, String>>?
        var classes: List<Map<String, String>>?

        FileReader("src/main/resources/rooms.csv").use { fileReader ->
            BufferedReader(fileReader).use { reader ->
                val contents = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()))

                val contentsCSV = normalizeRoomsCSV(contents)

                roomsMap = csvReader().readAllWithHeader(contentsCSV)
                roomsList = mapToRooms(roomsMap!!)
//                print(roomsList)
            }
        }

        FileReader("src/main/resources/classes.csv").use { fileReader ->
            BufferedReader(fileReader).use { reader ->
                val contents = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()))

                val contentsCSV = normalizeClassesCSV(contents)

                classes = csvReader().readAllWithHeader(contentsCSV)
                classesList = mapToClasses(classes!!)
//                print(classes)
            }
        }
   }
}