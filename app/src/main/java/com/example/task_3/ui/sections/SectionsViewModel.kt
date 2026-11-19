package com.example.task_3.ui.sections

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_3.domain.AttendanceStats
import com.example.task_3.domain.SportsRepository
import com.example.task_3.domain.SportsSection
import com.example.task_3.domain.calculateAttendance
import com.example.task_3.ui.ScreenStatus
import com.example.task_3.ui.loadScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SectionItemUiState(val section: SportsSection, val stats: AttendanceStats)

data class SectionsUiState(
    val status: ScreenStatus = ScreenStatus.LOADING,
    val query: String = "",
    val sections: List<SectionItemUiState> = emptyList(),
    val isSourceEmpty: Boolean = false,
    val selectedSectionId: String? = null,
    val error: Exception? = null,
)

@HiltViewModel
class SectionsViewModel @Inject constructor(
    private val repository: SportsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var sections: List<SportsSection> = emptyList()
    private val mutableUiState = MutableStateFlow(
        SectionsUiState(
            query = savedStateHandle[SEARCH_QUERY] ?: "",
            selectedSectionId = savedStateHandle[SELECTED_SECTION],
        ),
    )
    val uiState = mutableUiState.asStateFlow()

    init {
        reload()
    }

    fun updateQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
        mutableUiState.update { it.copy(query = query, sections = visibleSections(query)) }
    }

    fun selectSection(id: String) {
        savedStateHandle[SELECTED_SECTION] = id
        mutableUiState.update { it.copy(selectedSectionId = id) }
    }

    fun reload() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(status = ScreenStatus.LOADING, error = null) }
            loadScreen(
                onError = { error ->
                    mutableUiState.update { it.copy(status = ScreenStatus.ERROR, error = error) }
                },
            ) {
                sections = repository.getSections()
                mutableUiState.update {
                    it.copy(
                        status = ScreenStatus.READY,
                        sections = visibleSections(it.query),
                        isSourceEmpty = sections.isEmpty(),
                    )
                }
            }
        }
    }

    private fun visibleSections(query: String): List<SectionItemUiState> {
        val search = query.trim()
        return sections.filter {
            it.name.contains(search, ignoreCase = true) ||
                it.location.contains(search, ignoreCase = true)
        }.map { SectionItemUiState(it, calculateAttendance(it.groups)) }
    }

    private companion object {
        const val SEARCH_QUERY = "searchQuery"
        const val SELECTED_SECTION = "selectedSectionId"
    }
}
