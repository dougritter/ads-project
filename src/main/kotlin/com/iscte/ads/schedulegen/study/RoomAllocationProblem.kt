package com.iscte.ads.schedulegen.study

import com.iscte.ads.schedulegen.room.Room
import com.iscte.ads.schedulegen.schedule.StudentClass
import com.iscte.ads.schedulegen.schedule.TimeSlot
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem
import org.uma.jmetal.solution.integersolution.IntegerSolution
import org.uma.jmetal.util.bounds.Bounds
import org.uma.jmetal.util.pseudorandom.JMetalRandom
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

class RoomAllocationProblem(private val lectures: List<StudentClass>,
                            private val timeSlots: List<TimeSlot>,
                            private val rooms: List<Room>): AbstractIntegerProblem() {

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun getName(): String {
        return "roomAllocationProblem"
    }

    override fun evaluate(solution: IntegerSolution?): IntegerSolution {

        // make a copy of the lectures array
        // to apply a room to each one
        val lecturesWithRooms = lectures.toMutableList()

        // apply rooms to lectures
        solution?.variables()?.forEachIndexed { index, element ->
            // add the room to each class
            if (element != -1) {
                // -1 means that a room was not allocated
                lecturesWithRooms.add(lectures[index].copy(room = rooms[element]))
            }
        }

        // here, each lecture at `lecturesWithRooms`
        // it is ready to be evaluated

        var numberOfLecturesWithoutRoom = 0
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
        }

        // set evaluation
        solution!!.objectives()[0] = 0.0 // update objective quality

        println("evaluate room allocation solution \n${lecturesWithRooms}")
        return solution
    }

    override fun createSolution(): IntegerSolution {
        val roomTimeSlots = mutableListOf<MutableList<TimeSlot>>()
        // creates a list of slots for each room
        repeat(rooms.size) {
            roomTimeSlots.add(timeSlots.toMutableList())
        }

        val bounds = Bounds.create(0, rooms.size - 1)
        val newSolution = ScheduleIntegerSolution(lectures.size, 1, 0, mutableListOf(bounds))

        // run to find a random room for each lecture
        // matches the random room index with its list index
        // verifies if the slots of the random room are available
        // if not, generates a new random room index and try again
        lectures.forEach { currentLecture ->
            var randomRoomFound = false
            while (!randomRoomFound) {
                val randomRoom = JMetalRandom.getInstance().nextInt(bounds.lowerBound, bounds.upperBound)

                // returning if there is no start time - impossible to check slots
                if (currentLecture.startTime == null) {
                    newSolution.variables().add(-1)
                    randomRoomFound = true
                    break
                }

                // verify if room time slots are available
                val startTimeString = formatter.format(currentLecture.startTime)
                val slotIndex = roomTimeSlots[randomRoom].indexOfFirst { it.time == startTimeString &&
                        it.day == convertDayOfWeekToSlotDay(currentLecture.startTime.dayOfWeek) }

                if (slotIndex != -1 && roomTimeSlots[randomRoom][slotIndex].available &&
                    roomTimeSlots[randomRoom][slotIndex + currentLecture.slots-1].available) {
                    // slots are available
                    repeat(currentLecture.slots) {
                        //set slots as unavailable for new allocations - hard constraint
                        roomTimeSlots[randomRoom][slotIndex+(it)] = roomTimeSlots[randomRoom][slotIndex+(it)].copy(available = false)
                    }

                    randomRoomFound = true
                    newSolution.variables().add(randomRoom)
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