package com.example.task_3.ui.section

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_3.domain.AttendanceStats
import com.example.task_3.domain.SportsGroup
import com.example.task_3.domain.SportsRepository
import com.example.task_3.domain.SportsSection
import com.example.task_3.domain.calculateAttendance
import com.example.task_3.ui.ScreenStatus
import com.example.task_3.ui.loadScreen
import com.example.task_3.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroupItemUiState(val group: SportsGroup, val stats: AttendanceStats)

data class SectionUiState(
    val status: ScreenStatus = ScreenStatus.LOADING,
    val section: SportsSection? = null,
    val stats: AttendanceStats? = null,
    val groups: List<GroupItemUiState> = emptyList(),
    val selectedGroupId: String? = null,
    val error: Exception? = null,
)

@HiltViewModel
class SectionViewModel @Inject constructor(
    private val repository: SportsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val sectionId: String = savedStateHandle[Routes.SECTION_ID] ?: ""
    private val mutableUiState = MutableStateFlow(
        SectionUiState(selectedGroupId = savedStateHandle[SELECTED_GROUP]),
    )
    val uiState = mutableUiState.asStateFlow()

    init {
        reload()
    }

    fun selectGroup(id: String) {
        savedStateHandle[SELECTED_GROUP] = id
        mutableUiState.update { it.copy(selectedGroupId = id) }
    }

    fun reload() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(status = ScreenStatus.LOADING, error = null) }
            loadScreen(
                onError = { error ->
                    mutableUiState.update { it.copy(status = ScreenStatus.ERROR, error = error) }
                },
            ) {
                val section = repository.getSection(sectionId)
                mutableUiState.update {
                    it.copy(
                        status = if (section == null) ScreenStatus.NOT_FOUND else ScreenStatus.READY,
                        section = section,
                        stats = section?.let { value -> calculateAttendance(value.groups) },
                        groups = section?.groups?.map { group ->
                            GroupItemUiState(group, calculateAttendance(group))
                        }.orEmpty(),
                    )
                }
            }
        }
    }

    private companion object {
        const val SELECTED_GROUP = "selectedGroupId"
    }
}
