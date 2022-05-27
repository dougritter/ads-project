package com.iscte.ads.schedulegen.study

import org.uma.jmetal.algorithm.Algorithm
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder
import org.uma.jmetal.lab.experiment.ExperimentBuilder
import org.uma.jmetal.lab.experiment.component.impl.*
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm
import org.uma.jmetal.lab.experiment.util.ExperimentProblem
import org.uma.jmetal.operator.crossover.impl.SBXCrossover
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation
import org.uma.jmetal.problem.multiobjective.MultiobjectiveTSP
import org.uma.jmetal.problem.multiobjective.zdt.*
import org.uma.jmetal.qualityindicator.impl.*
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume
import org.uma.jmetal.solution.doublesolution.DoubleSolution
import org.uma.jmetal.util.errorchecking.JMetalException
import java.io.File
import java.io.IOException
import java.util.*

object NSGAIIStudy {
    private const val INDEPENDENT_RUNS = 1 // mudar para apenas 1 run
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {

//        if (args.size != 1) {
//            throw JMetalException("Missing argument: experimentBaseDirectory")
//        }
        val experimentBaseDirectory = javaClass.getResource("/")?.file
        val problemList: MutableList<ExperimentProblem<DoubleSolution>> = ArrayList()
        problemList.add(ExperimentProblem(ZDT1()))
        problemList.add(ExperimentProblem(ZDT2()))
        problemList.add(ExperimentProblem(ZDT3()))
        problemList.add(ExperimentProblem(ZDT4()))
        problemList.add(ExperimentProblem(ZDT6()))
        val algorithmList = configureAlgorithmList(problemList)
        val experiment = ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("NSGAIIStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory("resources/referenceFrontsCSV")
            .setIndicatorList(
                Arrays.asList(
                    Epsilon(),
                    Spread(),
                    GenerationalDistance(),
                    PISAHypervolume(),
                    NormalizedHypervolume(),
                    InvertedGenerationalDistance(),
                    InvertedGenerationalDistancePlus()
                )
            )
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build()
        ExecuteAlgorithms(experiment).run()

        // isto é para criar indicadores de performance dos algoritmos
//        ComputeQualityIndicators(experiment).run()
//        GenerateLatexTablesWithStatistics(experiment).run()
//        GenerateWilcoxonTestTablesWithR(experiment).run()
//        GenerateFriedmanTestTables(experiment).run()
//        GenerateBoxplotsWithR(experiment).setRows(2).setColumns(3).run()
    }

    /**
     * The algorithm list is composed of pairs [Algorithm] + [Problem] which form part of
     * a [ExperimentAlgorithm], which is a decorator for class [Algorithm]. The [ ] has an optional tag component, that can be set as it is shown in this
     * example, where four variants of a same algorithm are defined.
     */
    fun configureAlgorithmList(
        problemList: List<ExperimentProblem<DoubleSolution>>
    ): List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> {
        val algorithms: MutableList<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> = ArrayList()
        for (run in 0 until INDEPENDENT_RUNS) {
            for (i in problemList.indices) {
                val algorithm: Algorithm<List<DoubleSolution>> = NSGAIIBuilder(
                    problemList[i].problem,
                    SBXCrossover(1.0, 5.0),
                    PolynomialMutation(
                        1.0 / problemList[i].problem.numberOfVariables, 10.0
                    ),
                    100
                )
                    .setMaxEvaluations(25000)
                    .build()
                algorithms.add(ExperimentAlgorithm(algorithm, "NSGAIIa", problemList[i], run))
            }
            for (i in problemList.indices) {
                val algorithm: Algorithm<List<DoubleSolution>> = NSGAIIBuilder(
                    problemList[i].problem,
                    SBXCrossover(1.0, 20.0),
                    PolynomialMutation(
                        1.0 / problemList[i].problem.numberOfVariables, 20.0
                    ),
                    100
                )
                    .setMaxEvaluations(25000)
                    .build()
                algorithms.add(ExperimentAlgorithm(algorithm, "NSGAIIb", problemList[i], run))
            }
            for (i in problemList.indices) {
                val algorithm: Algorithm<List<DoubleSolution>> = NSGAIIBuilder(
                    problemList[i].problem,
                    SBXCrossover(1.0, 40.0),
                    PolynomialMutation(
                        1.0 / problemList[i].problem.numberOfVariables, 40.0
                    ),
                    10
                )
                    .setMaxEvaluations(25000)
                    .build()
                algorithms.add(ExperimentAlgorithm(algorithm, "NSGAIIc", problemList[i], run))
            }
            for (i in problemList.indices) {
                val algorithm: Algorithm<List<DoubleSolution>> = NSGAIIBuilder(
                    problemList[i].problem,
                    SBXCrossover(1.0, 80.0),
                    PolynomialMutation(
                        1.0 / problemList[i].problem.numberOfVariables, 80.0
                    ),
                    100
                )
                    .setMaxEvaluations(25000)
                    .build()
                algorithms.add(ExperimentAlgorithm(algorithm, "NSGAIId", problemList[i], run))
            }
        }
        return algorithms
    }
}