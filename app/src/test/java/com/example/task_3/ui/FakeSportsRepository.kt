package com.example.task_3.ui

import com.example.task_3.domain.SportsGroup
import com.example.task_3.domain.SportsRepository
import com.example.task_3.domain.SportsSection

internal class FakeSportsRepository(
    var sections: List<SportsSection> = sampleSections(),
    var failure: Exception? = null,
) : SportsRepository {
    override suspend fun getSections(): List<SportsSection> {
        failure?.let { throw it }
        return sections
    }
}

internal fun sampleSections(): List<SportsSection> = listOf(
    SportsSection(
        id = "boxing-center",
        name = "Бокс",
        location = "Центр · Лиговский проспект, 50",
        groups = listOf(
            SportsGroup("juniors", "Юниоры", 16, 12, 121),
            SportsGroup("adult", "Взрослая", 14, 12, 131),
        ),
    ),
    SportsSection(
        id = "judo-north",
        name = "Дзюдо",
        location = "Север · проспект Просвещения, 32",
        groups = listOf(SportsGroup("juniors", "Юниоры", 12, 12, 120)),
    ),
)
