package com.iscte.ads.schedulegen.study

import com.iscte.ads.schedulegen.datamapping.ObjectConverter
import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.room.RoomsGatewayImplementation
import com.iscte.ads.schedulegen.schedule.StudentClass
import com.iscte.ads.schedulegen.schedule.TimeSlot
import org.apache.commons.io.FileUtils
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder
import org.uma.jmetal.lab.experiment.Experiment
import org.uma.jmetal.lab.experiment.ExperimentBuilder
import org.uma.jmetal.lab.experiment.component.impl.*
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm
import org.uma.jmetal.lab.experiment.util.ExperimentProblem
import org.uma.jmetal.operator.crossover.impl.SBXCrossover
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation
import org.uma.jmetal.problem.doubleproblem.DoubleProblem
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume
import org.uma.jmetal.qualityindicator.impl.Spread
import org.uma.jmetal.solution.doublesolution.DoubleSolution
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


object NSGAIIStudy2 {
    private const val INDEPENDENT_RUNS = 1
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val objectConverter = ObjectConverter()
        val gateway = RoomsGatewayImplementation(objectConverter)

        ///////////////////////////////
        ////// scheduleGenProblem /////
        ///////////////////////////////
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


        val roomsCsv = NSGAIIStudy::class.java.getResource("/rooms.csv")?.readText()
        if (roomsCsv == null) {
            println("rooms file content is null - returning")
            return
        }

        val rooms: List<Room> = gateway.convertFromRoomsCsv(roomsCsv)
        ///////////////////////////////
        ////// scheduleGenProblem /////
        ///////////////////////////////


        //////////////////////////////////
        ////// roomAllocationProblem /////
        //////////////////////////////////
        val classesWithTimeCsv = NSGAIIStudy::class.java.getResource("/horario-semestre1.csv")?.readText()
        if (classesWithTimeCsv == null) {
            println("lectures with times file content is null - returning")
            return
        }
        val lecturesWithTime: List<StudentClass> = gateway.convertFromClassesCsv(classesWithTimeCsv)

        val timeSlotsRoomsCsv = NSGAIIStudy::class.java.getResource("/time-slots-all.csv")?.readText()
        if (timeSlotsRoomsCsv == null) {
            println("time slots of rooms file content is null - returning")
            return
        }

        val timeSlotsForRooms: List<TimeSlot> = gateway.convertFromTimeSlotsCsv(timeSlotsRoomsCsv)


        val roomsForRoomCsv = NSGAIIStudy::class.java.getResource("/rooms.csv")?.readText()
        if (roomsForRoomCsv == null) {
            println("rooms for room allocation problem file content is null - returning")
            return
        }

        val roomsForRoomsProblem: List<Room> = gateway.convertFromRoomsCsv(roomsForRoomCsv)


        //////////////////////////////////
        ////// roomAllocationProblem /////
        //////////////////////////////////
        val scheduleProblem = ScheduleGenProblem(lectures, timeSlots)
        val roomAllocationProblem = RoomAllocationProblem(lecturesWithTime, timeSlotsForRooms, roomsForRoomsProblem)


        val problemList: List<ExperimentProblem<DoubleSolution>> = listOf(ExperimentProblem(scheduleProblem), ExperimentProblem(roomAllocationProblem))
//        val problemList: List<ExperimentProblem<DoubleSolution>> = listOf(ExperimentProblem(roomAllocationProblem))
//        val problemList: List<ExperimentProblem<DoubleSolution>> = listOf(ExperimentProblem(scheduleProblem))

        val experimentBaseDirectory = "experimentBaseDirectory"

        // Apagar os dados da simulação anterior, não é feito pelo jMetal
        FileUtils.deleteDirectory(File(experimentBaseDirectory))
        //Files.createDirectories(Paths.get(experimentBaseDirectory))

        val algorithmList: MutableList<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> = configureAlgorithmList(problemList)
        val experiment: Experiment<DoubleSolution, List<DoubleSolution>> =
            ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("NSGAIIStudy")
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
        GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run()
        ComputeQualityIndicators(experiment).run()

        GenerateLatexTablesWithStatistics(experiment).run()
        GenerateWilcoxonTestTablesWithR(experiment).run()
        GenerateFriedmanTestTables(experiment).run();
        GenerateBoxplotsWithR(experiment).run()
//        GenerateHtmlPages(experiment).run()
    }

    fun configureAlgorithmList(problemList: List<ExperimentProblem<DoubleSolution>>): MutableList<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> {
        val algorithms: MutableList<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> = mutableListOf()
        for (run in 0 until INDEPENDENT_RUNS) {

            for (i in problemList.indices) {
                val algorithm =
                     NSGAIIBuilder(
                        problemList[i].problem,
                        SBXCrossover(1.0, 5.0),
                        PolynomialMutation(1.0, 20.0),
                        100
                    )
                    .setMaxEvaluations(25000)
                    .build()
                val experimentAlgorithm = ExperimentAlgorithm(algorithm!!, "NSGAIIa", problemList[i], run)
                algorithms.add(experimentAlgorithm)
            }

//            for (i in problemList.indices) {
//                val mutationProbability = 1.0 / problemList[i].problem.numberOfVariables
//                val mutationDistributionIndex = 20.0
//                val algorithm = SMPSOBuilder(
//                    problemList[i].problem as DoubleProblem,
//                    CrowdingDistanceArchive(100)
//                )
//                    .setMutation(PolynomialMutation(mutationProbability, mutationDistributionIndex))
//                    .setMaxIterations(250)
//                    .setSwarmSize(100)
//                    .setSolutionListEvaluator(SequentialSolutionListEvaluator())
//                    .build()


//                val algorithm = MOEADBuilder(problemList[i].problem, MOEADBuilder.Variant.MOEAD)
//                    .setCrossover(SBXCrossover(1.0, 5.0))
//                    .setMutation(PolynomialMutation(0.5, 10.0))
//                    .setPopulationSize(100)
//                    .build()

                /*
                public MOEADBuilder(Problem<DoubleSolution> problem, Variant variant) {
                    this.problem = problem ;
                    populationSize = 300 ;
                    resultPopulationSize = 300 ;
                    maxEvaluations = 150000 ;
                    crossover = new DifferentialEvolutionCrossover() ;
                    mutation = new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0);
                    functionType = MOEAD.FunctionType.TCHE ;
                    neighborhoodSelectionProbability = 0.1 ;
                    maximumNumberOfReplacedSolutions = 2 ;
                    dataDirectory = "" ;
                    neighborSize = 20 ;
                    numberOfThreads = 1 ;
                    moeadVariant = variant ;
                  }
                 */


//                val algorithm = NSGAIIIBuilder(problemList[i].problem)
//                    .setMaxIterations(250)
//                    .setPopulationSize(100)
//                    .setNumberOfDivisions(12)
//                    .build()

//                val experimentAlgorithm = ExperimentAlgorithm(algorithm!!, "MOEAD", problemList[i], run)
//                algorithms.add(experimentAlgorithm)
//            }
        }
        return algorithms
    }
}