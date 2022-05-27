package com.iscte.ads.schedulegen.study

import com.iscte.ads.schedulegen.schedule.Slot
import com.iscte.ads.schedulegen.schedule.StudentClass
import com.iscte.ads.schedulegen.schedule.TimeSlot
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem
import org.uma.jmetal.solution.Solution
import org.uma.jmetal.solution.integersolution.IntegerSolution
import org.uma.jmetal.util.bounds.Bounds
import org.uma.jmetal.util.pseudorandom.JMetalRandom

class ScheduleIntegerSolution(private val numberOfVariables: Int,
                              private val numberOfObjectives: Int,
                              private val numberOfConstraints: Int,
                              private val boundsList: List<Bounds<Int>>): IntegerSolution {

    private val variables = mutableListOf<Int>()
    private val objectives = DoubleArray(numberOfObjectives)
    private val constraints = DoubleArray(numberOfConstraints)

    override fun variables(): MutableList<Int> {
        return variables
    }

    override fun objectives(): DoubleArray = objectives

    override fun constraints(): DoubleArray = constraints

    override fun attributes(): MutableMap<Any, Any> = mutableMapOf()

    override fun copy(): Solution<Int> {
        val newSolution = ScheduleIntegerSolution(numberOfVariables, numberOfObjectives, numberOfConstraints, boundsList)
        newSolution.variables.addAll(variables)
        return newSolution
    }

    @Deprecated("Deprecated in Java")
    override fun getLowerBound(index: Int): Int = boundsList.first().lowerBound

    @Deprecated("Deprecated in Java")
    override fun getUpperBound(index: Int): Int = boundsList.first().upperBound

}

class ScheduleGenProblem(val lectures: List<StudentClass>,
                         val timeSlots: List<TimeSlot>): AbstractIntegerProblem() {

    override fun evaluate(solution: IntegerSolution?): IntegerSolution {
        println("should evaluate solution $solution")
        return solution!!
    }

    override fun createSolution(): IntegerSolution {
        val bounds = Bounds.create(0, timeSlots.size - 1)
        val newSolution = ScheduleIntegerSolution(lectures.size, 1, 0, mutableListOf(bounds))
        repeat(lectures.size) { newSolution.variables()[it] =
            JMetalRandom.getInstance().nextInt(bounds.lowerBound, bounds.upperBound) }
        return newSolution
    }

}