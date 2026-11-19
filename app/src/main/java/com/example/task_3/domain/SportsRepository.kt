package com.example.task_3.domain

interface SportsRepository {
    suspend fun getSections(): List<SportsSection>

    suspend fun getSection(id: String): SportsSection? =
        getSections().firstOrNull { it.id == id }

    suspend fun getGroup(sectionId: String, groupId: String): SportsGroup? =
        getSection(sectionId)?.groups?.firstOrNull { it.id == groupId }
}
