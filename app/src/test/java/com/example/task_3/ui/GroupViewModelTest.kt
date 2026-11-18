package com.example.task_3.ui

import androidx.lifecycle.SavedStateHandle
import com.example.task_3.domain.SportsGroup
import com.example.task_3.ui.group.GroupViewModel
import com.example.task_3.ui.navigation.Routes
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun sectionArgumentDisambiguatesGroupsWithTheSameId() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = GroupViewModel(
            FakeSportsRepository(),
            SavedStateHandle(
                mapOf(Routes.SECTION_ID to "judo-north", Routes.GROUP_ID to "juniors"),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ScreenStatus.READY, state.status)
        assertEquals("judo-north", state.section?.id)
        assertEquals("juniors", state.group?.id)
        assertEquals(12, state.group?.athleteCount)
        assertEquals(83, state.stats?.percent)
    }

    @Test
    fun savedRouteArgumentsRestoreTheSameGroupAndStatistics() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeSportsRepository()
            val savedState = SavedStateHandle(
                mapOf(Routes.SECTION_ID to "boxing-center", Routes.GROUP_ID to "juniors"),
            )
            val original = GroupViewModel(repository, savedState)
            advanceUntilIdle()
            val restored = GroupViewModel(repository, savedState)
            advanceUntilIdle()

            assertEquals(original.uiState.value, restored.uiState.value)
            assertEquals(63, restored.uiState.value.stats?.percent)
        }

    @Test
    fun unknownOrMissingRouteArgumentsProduceNotFound() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeSportsRepository()
        val handles = listOf(
            SavedStateHandle(
                mapOf(Routes.SECTION_ID to "unknown", Routes.GROUP_ID to "juniors"),
            ),
            SavedStateHandle(
                mapOf(Routes.SECTION_ID to "boxing-center", Routes.GROUP_ID to "unknown"),
            ),
            SavedStateHandle(),
        )

        handles.forEach { savedState ->
            val viewModel = GroupViewModel(repository, savedState)
            advanceUntilIdle()

            assertEquals(ScreenStatus.NOT_FOUND, viewModel.uiState.value.status)
            assertNull(viewModel.uiState.value.group)
            assertNull(viewModel.uiState.value.stats)
        }
    }

    @Test
    fun groupWithoutTrainingHasNoAttendancePercentage() = runTest(mainDispatcherRule.dispatcher) {
        val group = SportsGroup("new-group", "Новая группа", 8, 0, 0)
        val section = sampleSections().first().copy(groups = listOf(group))
        val viewModel = GroupViewModel(
            FakeSportsRepository(listOf(section)),
            SavedStateHandle(mapOf(Routes.SECTION_ID to section.id, Routes.GROUP_ID to group.id)),
        )

        advanceUntilIdle()

        assertEquals(ScreenStatus.READY, viewModel.uiState.value.status)
        assertEquals(group.id, viewModel.uiState.value.group?.id)
        assertEquals(0, viewModel.uiState.value.stats?.possibleVisits)
        assertNull(viewModel.uiState.value.stats?.percent)
    }

    @Test
    fun preservesLoadErrorAndReloadsTheSameGroupAfterRecovery() =
        runTest(mainDispatcherRule.dispatcher) {
            val error = IOException("Demo data is unavailable")
            val repository = FakeSportsRepository(failure = error)
            val viewModel = GroupViewModel(
                repository,
                SavedStateHandle(
                    mapOf(Routes.SECTION_ID to "boxing-center", Routes.GROUP_ID to "juniors"),
                ),
            )
            advanceUntilIdle()

            assertEquals(ScreenStatus.ERROR, viewModel.uiState.value.status)
            assertSame(error, viewModel.uiState.value.error)

            repository.failure = null
            viewModel.reload()
            advanceUntilIdle()

            assertEquals(ScreenStatus.READY, viewModel.uiState.value.status)
            assertEquals("juniors", viewModel.uiState.value.group?.id)
            assertEquals(63, viewModel.uiState.value.stats?.percent)
            assertNull(viewModel.uiState.value.error)
        }
}
