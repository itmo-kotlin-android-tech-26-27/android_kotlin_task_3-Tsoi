package com.example.task_3.domain

data class SportsGroup(
    val id: String,
    val name: String,
    val athleteCount: Int,
    val trainingCount: Int,
    val attendedVisits: Int,
) {
    init {
        require(id.isNotBlank()) { "Group id must not be blank" }
        require(name.isNotBlank()) { "Group name must not be blank" }
        require(athleteCount >= 0) { "Athlete count must not be negative" }
        require(trainingCount >= 0) { "Training count must not be negative" }
        val possibleVisits = athleteCount.toLong() * trainingCount
        require(possibleVisits <= Int.MAX_VALUE) { "Possible visit count is too large" }
        require(attendedVisits.toLong() in 0..possibleVisits) {
            "Attended visits must be between zero and possible visits"
        }
    }
}
