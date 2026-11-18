package com.example.task_3.domain

data class SportsSection(
    val id: String,
    val name: String,
    val location: String,
    val groups: List<SportsGroup>,
) {
    init {
        require(id.isNotBlank()) { "Section id must not be blank" }
        require(name.isNotBlank()) { "Section name must not be blank" }
        require(location.isNotBlank()) { "Section location must not be blank" }
        require(groups.map { it.id }.distinct().size == groups.size) {
            "Group ids must be unique within a section"
        }
    }
}
