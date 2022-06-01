package com.iscte.ads.schedulegen.study

import com.iscte.ads.schedulegen.datamapping.ObjectConverter
import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.room.RoomsGatewayImplementation
import com.iscte.ads.schedulegen.schedule.StudentClass
import com.iscte.ads.schedulegen.schedule.TimeSlot
import org.uma.jmetal.operator.crossover.impl.IntegerSBXCrossover
import org.uma.jmetal.operator.mutation.impl.IntegerPolynomialMutation
import org.uma.jmetal.solution.integersolution.IntegerSolution
import java.io.File

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Arrays

import org.apache.commons.io.FileUtils
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder
import org.uma.jmetal.lab.experiment.Experiment
import org.uma.jmetal.lab.experiment.ExperimentBuilder
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms
import org.uma.jmetal.lab.experiment.component.impl.GenerateReferenceParetoFront
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm
import org.uma.jmetal.lab.experiment.util.ExperimentProblem
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume
import org.uma.jmetal.qualityindicator.impl.Spread


object NSGAIIStudy2 {
    private const val INDEPENDENT_RUNS = 1
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val objectConverter = ObjectConverter()
        val gateway = RoomsGatewayImplementation(objectConverter)

        //val classesCsv = NSGAIIStudy::class.java.getResource("/horario-semestre1-vazio.csv")?.readText()
        val classesCsv = NSGAIIStudy::class.java.getResource("/horario-semestre1.csv")?.readText()

        if (classesCsv == null) {
            println("lectures file content is null - returning")
            return
        }
        val lectures: List<StudentClass> = gateway.convertFromClassesCsv(classesCsv)

        val timeSlotsCsv = NSGAIIStudy::class.java.getResource("/time-slots-all.csv")?.readText()
        if (timeSlotsCsv == null) {
            println("time slots file content is null - returning")
            return
        }

        val timeSlots: List<TimeSlot> = gateway.convertFromTimeSlotsCsv(timeSlotsCsv)


        val roomsCsv = NSGAIIStudy::class.java.getResource("/rooms.csv")?.readText()
        if (roomsCsv == null) {
            println("rooms file content is null - returning")
            return
        }

        val rooms: List<Room> = gateway.convertFromRoomsCsv(roomsCsv)
        val classesWithTimeCsv = NSGAIIStudy::class.java.getResource("/horario-semestre1.csv")?.readText()

        if (classesWithTimeCsv == null) {
            println("lectures with times file content is null - returning")
            return
        }
        val lecturesWithTime: List<StudentClass> = gateway.convertFromClassesCsv(classesWithTimeCsv)

        val scheduleProblem = ScheduleGenProblem(lectures, timeSlots)
        val roomAllocationProblem = RoomAllocationProblem(lecturesWithTime, timeSlots, rooms)


        val problemList: List<ExperimentProblem<IntegerSolution>> = listOf(ExperimentProblem(scheduleProblem), ExperimentProblem(roomAllocationProblem))
        //val problemList: List<ExperimentProblem<IntegerSolution>> = listOf(ExperimentProblem(roomAllocationProblem))
        //val problemList: List<ExperimentProblem<IntegerSolution>> = listOf(ExperimentProblem(scheduleProblem))

        val experimentBaseDirectory = "experimentBaseDirectory"

        // Apagar os dados da simulação anterior, não é feito pelo jMetal
        FileUtils.deleteDirectory(File(experimentBaseDirectory))
        //Files.createDirectories(Paths.get(experimentBaseDirectory))

        val algorithmList: MutableList<ExperimentAlgorithm<IntegerSolution, List<IntegerSolution>>> = configureAlgorithmList(problemList)
        val experiment: Experiment<IntegerSolution, List<IntegerSolution>> =
            ExperimentBuilder<IntegerSolution, List<IntegerSolution>>("NSGAIIStudy")
                .setAlgorithmList(algorithmList)
                .setProblemList(problemList)
                .setExperimentBaseDirectory(experimentBaseDirectory)
                .setOutputParetoFrontFileName("FUN")
                .setOutputParetoSetFileName("VAR")
                .setReferenceFrontDirectory("resources/referenceFrontsCSV")
                .setIndicatorList(
                    Arrays.asList(
                        Spread(),
                        NormalizedHypervolume()
                    )
                )
                .setIndependentRuns(INDEPENDENT_RUNS)
                .setNumberOfCores(8)
                .build()


        // Apagar os dados da simulação anterior, não é feito pelo jMetal
        FileUtils.deleteDirectory(File("resources/referenceFrontsCSV"))
        // A diretoria tem de existir, não é criada pelo jMetal
        Files.createDirectories(Paths.get("resources/referenceFrontsCSV"))
        ExecuteAlgorithms(experiment).run()
        GenerateReferenceParetoFront(experiment).run()
        ComputeQualityIndicators(experiment).run()
    }

    fun configureAlgorithmList(problemList: List<ExperimentProblem<IntegerSolution>>): MutableList<ExperimentAlgorithm<IntegerSolution, List<IntegerSolution>>> {
        val algorithms: MutableList<ExperimentAlgorithm<IntegerSolution, List<IntegerSolution>>> = mutableListOf()
        for (run in 0 until INDEPENDENT_RUNS) {
            for (i in problemList.indices) {
                val algorithm =
                     NSGAIIBuilder(
                        problemList[i].problem,
                        IntegerSBXCrossover(1.0, 5.0),
                        IntegerPolynomialMutation(1.0 / problemList[i].problem.numberOfVariables, 10.0),
                        100
                    )
                    .setMaxEvaluations(25000)
                    .build()
                val experimentAlgorithm = ExperimentAlgorithm(algorithm!!, "NSGAIIa", problemList[i], run)
                algorithms.add(experimentAlgorithm)
            }
        }
        return algorithms
    }
}