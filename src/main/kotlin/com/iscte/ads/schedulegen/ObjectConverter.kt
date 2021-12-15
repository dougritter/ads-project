package com.iscte.ads.schedulegen

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.stereotype.Service

@Service
class ObjectConverter {
    private val mapper = ObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())

    init {
        mapper.findAndRegisterModules()
    }

    fun convertToJson(arrayOfEvents: Array<Event>): String {
        return mapper.writeValueAsString(arrayOfEvents)
    }

    fun normalizeClassesCSV(csv: String): String {
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

    fun revertClassesCSV(csv: String): String {
        var result = csv
        result = result.replaceFirst("course", "Curso")
        result = result.replaceFirst("executionUnit", "Unidade de execução")
        result = result.replaceFirst("shift", "Turno")
        result = result.replaceFirst("classIdentifier", "Turma")
        result = result.replaceFirst("subscribersCount", "Inscritos no turno (no 1º semestre é baseado em estimativas)")

        result = result.replaceFirst("startTime", "Início")
        result = result.replaceFirst("endTime", "Fim")

        return result
    }

    fun normalizeRoomsCSV(csv: String): String {
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

}