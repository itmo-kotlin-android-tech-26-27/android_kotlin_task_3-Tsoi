package com.example.task_3.ui

import androidx.lifecycle.SavedStateHandle
import com.example.task_3.ui.navigation.Routes
import com.example.task_3.ui.section.SectionViewModel
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SectionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadsOnlyRequestedSectionAndRestoresSelectedGroup() =
        runTest(mainDispatcherRule.dispatcher) {
            val savedState = SavedStateHandle(
                mapOf(Routes.SECTION_ID to "boxing-center", "selectedGroupId" to "juniors"),
            )
            val viewModel = SectionViewModel(FakeSportsRepository(), savedState)

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(ScreenStatus.READY, state.status)
            assertEquals("boxing-center", state.section?.id)
            assertEquals("juniors", state.selectedGroupId)
            assertEquals(listOf("juniors", "adult"), state.groups.map { it.group.id })
            assertEquals(30, state.stats?.athleteCount)
            assertEquals(63, state.groups.first().stats.percent)
        }

    @Test
    fun selectedGroupIsSavedAndRestoredInNewViewModel() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeSportsRepository()
        val savedState = SavedStateHandle(mapOf(Routes.SECTION_ID to "boxing-center"))
        val viewModel = SectionViewModel(repository, savedState)
        advanceUntilIdle()

        viewModel.selectGroup("adult")
        val restored = SectionViewModel(repository, savedState)
        advanceUntilIdle()

        assertEquals("adult", viewModel.uiState.value.selectedGroupId)
        assertEquals("adult", savedState.get<String>("selectedGroupId"))
        assertEquals("adult", restored.uiState.value.selectedGroupId)
    }

    @Test
    fun unknownOrMissingSectionIdProducesNotFound() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeSportsRepository()
        val handles = listOf(
            SavedStateHandle(mapOf(Routes.SECTION_ID to "unknown")),
            SavedStateHandle(),
        )

        handles.forEach { savedState ->
            val viewModel = SectionViewModel(repository, savedState)
            advanceUntilIdle()

            assertEquals(ScreenStatus.NOT_FOUND, viewModel.uiState.value.status)
            assertNull(viewModel.uiState.value.section)
            assertTrue(viewModel.uiState.value.groups.isEmpty())
            assertNull(viewModel.uiState.value.stats)
        }
    }

    @Test
    fun sectionWithoutGroupsIsReadyWithEmptyStatistics() = runTest(mainDispatcherRule.dispatcher) {
        val section = sampleSections().first().copy(groups = emptyList())
        val viewModel = SectionViewModel(
            FakeSportsRepository(listOf(section)),
            SavedStateHandle(mapOf(Routes.SECTION_ID to section.id)),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ScreenStatus.READY, state.status)
        assertEquals(section.id, state.section?.id)
        assertTrue(state.groups.isEmpty())
        assertEquals(0, state.stats?.groupCount)
        assertNull(state.stats?.percent)
    }

    @Test
    fun preservesLoadErrorAndReloadsRequestedSectionAfterRecovery() =
        runTest(mainDispatcherRule.dispatcher) {
            val error = IOException("Demo data is unavailable")
            val repository = FakeSportsRepository(failure = error)
            val viewModel = SectionViewModel(
                repository,
                SavedStateHandle(mapOf(Routes.SECTION_ID to "boxing-center")),
            )
            advanceUntilIdle()

            assertEquals(ScreenStatus.ERROR, viewModel.uiState.value.status)
            assertSame(error, viewModel.uiState.value.error)

            repository.failure = null
            viewModel.reload()
            advanceUntilIdle()

            assertEquals(ScreenStatus.READY, viewModel.uiState.value.status)
            assertEquals("boxing-center", viewModel.uiState.value.section?.id)
            assertNull(viewModel.uiState.value.error)
        }
}
