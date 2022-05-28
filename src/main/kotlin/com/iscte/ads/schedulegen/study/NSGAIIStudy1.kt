package com.iscte.ads.schedulegen.study

import com.iscte.ads.schedulegen.datamapping.ObjectConverter
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
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;


object NSGAIIStudy2 {
    private const val INDEPENDENT_RUNS = 1
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val objectConverter = ObjectConverter()
        val gateway = RoomsGatewayImplementation(objectConverter)

        val classesCsv = NSGAIIStudy::class.java.getResource("/horario-semestre1-vazio.csv")?.readText()

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

        val scheduleProblem = ScheduleGenProblem(lectures, timeSlots)

        val experimentBaseDirectory = "experimentBaseDirectory"

        // Apagar os dados da simulação anterior, não é feito pelo jMetal
        FileUtils.deleteDirectory(File(experimentBaseDirectory))
        val problemList: List<ExperimentProblem<IntegerSolution>> = listOf(ExperimentProblem(scheduleProblem))

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
//        GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run()
//        ComputeQualityIndicators(experiment).run()
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