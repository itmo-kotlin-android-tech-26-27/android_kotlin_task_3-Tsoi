package com.example.task_3.domain

import kotlin.math.roundToInt

data class AttendanceStats(
    val athleteCount: Int,
    val groupCount: Int,
    val attendedVisits: Int,
    val possibleVisits: Int,
    val percent: Int?,
) {
    val progress: Float
        get() = if (possibleVisits == 0) 0f else attendedVisits.toFloat() / possibleVisits
}

fun calculateAttendance(groups: List<SportsGroup>): AttendanceStats {
    val athleteCount = groups.sumOf { it.athleteCount.toLong() }.toVisitCount()
    val attendedVisits = groups.sumOf { it.attendedVisits.toLong() }.toVisitCount()
    val possibleVisits = groups.sumOf {
        it.athleteCount.toLong() * it.trainingCount
    }.toVisitCount()

    return AttendanceStats(
        athleteCount = athleteCount,
        groupCount = groups.size,
        attendedVisits = attendedVisits,
        possibleVisits = possibleVisits,
        percent = if (possibleVisits == 0) {
            null
        } else {
            (attendedVisits.toDouble() / possibleVisits * PERCENT_SCALE).roundToInt()
        },
    )
}

fun calculateAttendance(group: SportsGroup): AttendanceStats =
    calculateAttendance(listOf(group))

private fun Long.toVisitCount(): Int {
    require(this <= Int.MAX_VALUE) { "Combined count is too large" }
    return toInt()
}

private const val PERCENT_SCALE = 100
