package com.iscte.ads.schedulegen

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.BufferedReader
import java.io.FileReader
import java.util.stream.Collectors

class RoomsGateway() {

    private fun mapToClasses(classesList: List<Map<String, String>>): MutableList<StudentClass> {
        val studentClasses = mutableListOf<StudentClass>()

        for (item in classesList) {
            val subscribersCount = item["Inscritos no turno (no 1º semestre é baseado em estimativas)"].orEmpty()

            studentClasses.add(StudentClass(
                    course = item["Curso"].orEmpty(),
                    executionUnit = item["Unidade de execução"].orEmpty(),
                    shift = item["Turno"].orEmpty(),
                    classIdentifier = item["Turma"].orEmpty(),
                    subscribersCount =  if (subscribersCount.isNotEmpty()) subscribersCount.toInt() else 0
            ))
        }

        return studentClasses
    }

    fun loadFile() {
        var rooms: List<Map<String, String>>?
        var classes: List<Map<String, String>>?

        FileReader("src/main/resources/rooms.csv").use { fileReader ->
            BufferedReader(fileReader).use { reader ->
                val contents = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()))

                val contentsCSV = contents.replace(";", ",")

                rooms = csvReader().readAllWithHeader(contentsCSV)
                print(rooms)
            }
        }

        FileReader("src/main/resources/classes.csv").use { fileReader ->
            BufferedReader(fileReader).use { reader ->
                val contents = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()))

                var contentsCSV = contents.replace(",", "-")
                contentsCSV = contentsCSV.replace(";", ",")

                classes = csvReader().readAllWithHeader(contentsCSV)
                val studentClasses = mapToClasses(classes!!)
                print(classes)
            }
        }
   }
}