package com.iscte.ads.schedulegen.study

import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.schedule.StudentClass
import com.iscte.ads.schedulegen.schedule.TimeSlot
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem
import org.uma.jmetal.solution.doublesolution.DoubleSolution
import org.uma.jmetal.solution.integersolution.IntegerSolution
import org.uma.jmetal.util.bounds.Bounds
import org.uma.jmetal.util.pseudorandom.JMetalRandom
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter


class RoomAllocationProblem(private val lectures: List<StudentClass>,
                            private val timeSlots: List<TimeSlot>,
                            private val rooms: List<Room>): AbstractDoubleProblem() {

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun getName(): String {
        return "roomAllocationProblem"
    }

    override fun evaluate(solution: DoubleSolution?): DoubleSolution {
        val f = DoubleArray(solution!!.objectives().size)
        println("evaluate room allocation solution \n${f[0]}, \n${f[1]} ")
        // make a copy of the lectures array
        // to apply a room to each one
        val lecturesWithRooms = lectures.toMutableList()

        // apply rooms to lectures
        solution?.variables()?.forEachIndexed { index, element ->
            // add the room to each class
            if (element.toInt() != -1) {
                // -1 means that a room was not allocated
                lecturesWithRooms.add(lectures[index].copy(room = rooms[element.toInt()]))
            }
        }

        // here, each lecture at `lecturesWithRooms`
        // it is ready to be evaluated

        var numberOfLecturesWithoutRoom = 0
        var numberOfLectures = 0
        var overbookingCases = 0
        lecturesWithRooms.forEach { lecture ->

            // verify number of lectures without a room
            if (lecture.room == null) {
                numberOfLecturesWithoutRoom++
            } else {

                // verify if room has enough capacity for the class
                if (lecture.room.normalCapacity < lecture.subscribersCount) {
                    overbookingCases++
                }
            }
            numberOfLectures++
        }

        // set evaluation
        f[0] = (numberOfLecturesWithoutRoom/numberOfLectures).toDouble()
        f[1] = (overbookingCases/numberOfLectures).toDouble()
        // update objective quality 1 and 2
        solution!!.objectives()[0] = f[0]
        solution!!.objectives()[1] = f[1]

        println("evaluate room allocation solution \n${f[0]}, \n${f[1]} ")
        return solution
    }


    override fun createSolution(): DoubleSolution {
        val roomTimeSlots = mutableListOf<MutableList<TimeSlot>>()
        // creates a list of slots for each room
        repeat(rooms.size) {
            roomTimeSlots.add(timeSlots.toMutableList())
        }

        val bounds = mutableListOf<Bounds<Double>>()
        repeat(20000) {
            bounds.add(Bounds.create(0.0, (rooms.size - 1).toDouble()))
        }

        val newSolution = ScheduleDoubleSolution(2, 2, bounds)
        newSolution.variables().clear()

        // run to find a random room for each lecture
        // matches the random room index with its list index
        // verifies if the slots of the random room are available
        // if not, generates a new random room index and try again
        lectures.forEach { currentLecture ->
            val randomRoom = JMetalRandom.getInstance().nextDouble(bounds.first().lowerBound, bounds.first().upperBound)

            // returning if there is no start time - impossible to check slots
            if (currentLecture.startTime == null) {
                newSolution.variables().add(-1.0)
            } else {
                // verify if room time slots are available
                val startTimeString = formatter.format(currentLecture.startTime)
                val slotIndex = roomTimeSlots[randomRoom.toInt()].indexOfFirst {
                    it.time == startTimeString &&
                            it.day == convertDayOfWeekToSlotDay(currentLecture.startTime.dayOfWeek)
                }

                if (slotIndex != -1 && roomTimeSlots[randomRoom.toInt()][slotIndex].available &&
                    roomTimeSlots[randomRoom.toInt()][slotIndex + currentLecture.slots - 1].available
                ) {
                    // slots are available
                    repeat(currentLecture.slots) {
                        //set slots as unavailable for new allocations - hard constraint
                        roomTimeSlots[randomRoom.toInt()][slotIndex + (it)] =
                            roomTimeSlots[randomRoom.toInt()][slotIndex + (it)].copy(available = false)
                    }

                    newSolution.variables().add(randomRoom)
                } else {
                    newSolution.variables().add(-1.0)
                }
            }
        }

        return newSolution
    }

    private fun convertDayOfWeekToSlotDay(dayOfWeek: DayOfWeek): String {
        return when(dayOfWeek) {
            DayOfWeek.MONDAY -> "Seg"
            DayOfWeek.TUESDAY -> "Ter"
            DayOfWeek.WEDNESDAY -> "Qua"
            DayOfWeek.THURSDAY -> "Qui"
            DayOfWeek.FRIDAY -> "Sex"
            else -> { "Sab" }
        }
    }
}
