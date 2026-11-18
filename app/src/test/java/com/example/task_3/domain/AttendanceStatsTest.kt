package com.example.task_3.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AttendanceStatsTest {
    @Test
    fun sectionAttendanceUsesAllPossibleVisitsInsteadOfAveragingPercentages() {
        val groups = listOf(
            group(athleteCount = 1, trainingCount = 1, attendedVisits = 1),
            group(id = "large", athleteCount = 9, trainingCount = 1, attendedVisits = 0),
        )

        val stats = calculateAttendance(groups)

        assertEquals(10, stats.athleteCount)
        assertEquals(2, stats.groupCount)
        assertEquals(1, stats.attendedVisits)
        assertEquals(10, stats.possibleVisits)
        assertEquals(10, stats.percent)
    }

    @Test
    fun boxingDemoAttendanceIsCalculatedFromVisits() {
        val groups = listOf(
            group(id = "initial", athleteCount = 18, trainingCount = 12, attendedVisits = 177),
            group(id = "juniors", athleteCount = 16, trainingCount = 12, attendedVisits = 121),
            group(id = "senior", athleteCount = 20, trainingCount = 12, attendedVisits = 218),
            group(id = "adult", athleteCount = 14, trainingCount = 12, attendedVisits = 131),
        )

        assertEquals(AttendanceStats(68, 4, 647, 816, 79), calculateAttendance(groups))
        assertEquals(63, calculateAttendance(groups[1]).percent)
    }

    @Test
    fun attendanceRoundsToNearestWholePercent() {
        assertEquals(67, calculateAttendance(group(attendedVisits = 2)).percent)
    }

    @Test
    fun noAttendedVisitsReturnsZeroPercent() {
        assertEquals(0, calculateAttendance(group(attendedVisits = 0)).percent)
    }

    @Test
    fun allPossibleVisitsReturnsOneHundredPercent() {
        assertEquals(100, calculateAttendance(group(attendedVisits = 3)).percent)
    }

    @Test
    fun noTrainingReturnsUnavailablePercentage() {
        val stats = calculateAttendance(group(trainingCount = 0, attendedVisits = 0))

        assertEquals(1, stats.athleteCount)
        assertEquals(0, stats.possibleVisits)
        assertNull(stats.percent)
    }

    @Test
    fun noAthletesReturnsUnavailablePercentage() {
        assertNull(calculateAttendance(group(athleteCount = 0, attendedVisits = 0)).percent)
    }

    @Test
    fun emptySectionReturnsEmptyStatistics() {
        assertEquals(AttendanceStats(0, 0, 0, 0, null), calculateAttendance(emptyList()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsMoreVisitsThanPossible() {
        group(attendedVisits = 4)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNegativeAthleteCount() {
        group(athleteCount = -1, attendedVisits = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNegativeTrainingCount() {
        group(trainingCount = -1, attendedVisits = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNegativeAttendedVisits() {
        group(attendedVisits = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankGroupName() {
        group().copy(name = " ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankGroupId() {
        group().copy(id = "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankSectionLocation() {
        SportsSection("boxing", "Бокс", " ", emptyList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsRepeatedGroupIdsWithinSection() {
        SportsSection("boxing", "Бокс", "Центральный зал", listOf(group(), group()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsOverflowingPossibleVisits() {
        group(athleteCount = Int.MAX_VALUE, trainingCount = 2, attendedVisits = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsOverflowingCombinedCounts() {
        calculateAttendance(
            listOf(
                group(athleteCount = Int.MAX_VALUE, trainingCount = 0, attendedVisits = 0),
                group(id = "second", trainingCount = 0, attendedVisits = 0),
            ),
        )
    }

    private fun group(
        id: String = "group",
        athleteCount: Int = 1,
        trainingCount: Int = 3,
        attendedVisits: Int = 2,
    ): SportsGroup = SportsGroup(id, "Группа", athleteCount, trainingCount, attendedVisits)
}
