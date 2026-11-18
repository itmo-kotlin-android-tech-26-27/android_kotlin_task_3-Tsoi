package com.example.task_3.ui.group

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

data class GroupUiState(
    val status: ScreenStatus = ScreenStatus.LOADING,
    val section: SportsSection? = null,
    val group: SportsGroup? = null,
    val stats: AttendanceStats? = null,
    val error: Exception? = null,
)

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: SportsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val sectionId: String = savedStateHandle[Routes.SECTION_ID] ?: ""
    private val groupId: String = savedStateHandle[Routes.GROUP_ID] ?: ""
    private val mutableUiState = MutableStateFlow(GroupUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        reload()
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
                val group = section?.groups?.firstOrNull { it.id == groupId }
                mutableUiState.value = GroupUiState(
                    status = if (group == null) ScreenStatus.NOT_FOUND else ScreenStatus.READY,
                    section = section,
                    group = group,
                    stats = group?.let(::calculateAttendance),
                )
            }
        }
    }
}
