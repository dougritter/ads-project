package com.iscte.ads.schedulegen.study

import com.iscte.ads.schedulegen.schedule.Slot
import com.iscte.ads.schedulegen.schedule.StudentClass
import com.iscte.ads.schedulegen.schedule.TimeSlot
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem
import org.uma.jmetal.solution.Solution
import org.uma.jmetal.solution.integersolution.IntegerSolution
import org.uma.jmetal.util.bounds.Bounds
import org.uma.jmetal.util.pseudorandom.JMetalRandom
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    override fun evaluate(solution: IntegerSolution?): IntegerSolution {
        val listCopy = lectures.toMutableList()
        var totalSum = 0.0

        var errorSlotsInDifferentDays = 0

        // apply time slots to lectures
        solution?.variables()?.forEachIndexed { index, timeSlotIndex ->
            totalSum += timeSlotIndex

            val startSlot = timeSlots[timeSlotIndex]
            val slotsRequired = listCopy[index].slots
            if (slotsRequired > 1 && timeSlots.size > timeSlotIndex + (listCopy[index].slots - 1)) {
                val endSlot = timeSlots[timeSlotIndex + (listCopy[index].slots - 1)]
                listCopy[index] = listCopy[index].copy(startTime = convertToDateTime(startSlot), endTime = convertToDateTime(endSlot))

                // count errors of slots allocated in different days
                if (startSlot.day != endSlot.day) {
                    errorSlotsInDifferentDays++
                    // apply penalty to totalSum
                    totalSum = timeSlotIndex - totalSum * 0.1
                }
            } else {
                listCopy[index] = listCopy[index].copy(startTime = convertToDateTime(startSlot))
            }
        }

        // evaluate classes in the same day
        // hard constraint: different classes can't be at the same time (collision)
        // soft constraint: gaps between classes at the same day
        var gapsCount = 0
        val collisions = mutableListOf<Int>()

        val lecturesInWeekDays = mutableListOf<List<StudentClass>>()

        // Filter for classes that are in the same day as others
        lecturesInWeekDays.add(listCopy.filter { it.startTime?.dayOfWeek == DayOfWeek.MONDAY })
        lecturesInWeekDays.add(listCopy.filter { it.startTime?.dayOfWeek == DayOfWeek.TUESDAY })
        lecturesInWeekDays.add(listCopy.filter { it.startTime?.dayOfWeek == DayOfWeek.WEDNESDAY })
        lecturesInWeekDays.add(listCopy.filter { it.startTime?.dayOfWeek == DayOfWeek.THURSDAY })
        lecturesInWeekDays.add(listCopy.filter { it.startTime?.dayOfWeek == DayOfWeek.FRIDAY })
        lecturesInWeekDays.add(listCopy.filter { it.startTime?.dayOfWeek == DayOfWeek.SATURDAY })

        lecturesInWeekDays.forEach { lecturesInTheSameDay ->
            lecturesInTheSameDay.forEachIndexed { itemIndex, item ->
                if (!collisions.contains(itemIndex) && item.startTime != null && item.endTime != null) {

                    lecturesInTheSameDay.forEachIndexed { otherIndex, other ->

                        if (!collisions.contains(otherIndex) && otherIndex != itemIndex
                            && other.startTime != null && other.endTime != null) {

                            // Check if other starts after
                            if (other.startTime.isAfter(item.startTime)) {

                                // verify collision
                                if (!other.endTime.isAfter(item.endTime)) {
                                    // Collision exists: other starts after the first one but ends before first one end
                                    collisions.add(itemIndex)
                                    collisions.add(otherIndex)
                                } else {
                                    // There is no collision, verify gap between lectures
                                    val idleTime = ChronoUnit.MINUTES.between(item.endTime, other.startTime)
                                    if (idleTime > 30) {
                                        gapsCount++
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        if (totalSum < 0.0) {
            totalSum = 0.0
        }

        println("\n\n\n\nevaluate " +
                "\nError lectures with slots in different days: $errorSlotsInDifferentDays " +
                "\nCollisions: ${collisions.size} " +
                "\nGaps greater than 30min between lectures in the same day $gapsCount" +
                "\nQuality: $totalSum")
        solution!!.objectives()[0] = totalSum

        return solution
    }

    override fun getName(): String {
        return "scheduleProblem"
    }

    override fun createSolution(): IntegerSolution {
        val bounds = Bounds.create(0, timeSlots.size - 1)
        val newSolution = ScheduleIntegerSolution(lectures.size, 1, 0, mutableListOf(bounds))
        repeat(lectures.size) {
            newSolution.variables().add(JMetalRandom.getInstance().nextInt(bounds.lowerBound, bounds.upperBound))
        }
        return newSolution
    }

    private fun convertToDateTime(timeSlot: TimeSlot): LocalDateTime {
        val day = when(timeSlot.day) {
            "Seg" -> "21-09-2015"
            "Ter" -> "22-09-2015"
            "Qua" -> "23-09-2015"
            "Qui" -> "24-09-2015"
            "Sex" -> "25-09-2015"
            else -> { "26-09-2015" }
        }

        return LocalDateTime.parse("$day ${timeSlot.time}", formatter)
    }
}