package com.example.task_3.ui

import androidx.lifecycle.SavedStateHandle
import com.example.task_3.ui.sections.SectionsViewModel
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SectionsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun restoresQueryAndAppliesCaseInsensitiveTrimmedSearch() =
        runTest(mainDispatcherRule.dispatcher) {
            val savedState = SavedStateHandle(mapOf("searchQuery" to " бОкС "))
            val viewModel = SectionsViewModel(FakeSportsRepository(), savedState)

            assertEquals(ScreenStatus.LOADING, viewModel.uiState.value.status)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(ScreenStatus.READY, state.status)
            assertEquals(" бОкС ", state.query)
            assertEquals(listOf("boxing-center"), state.sections.map { it.section.id })
        }

    @Test
    fun unknownQueryProducesEmptyResultsWithoutMarkingSourceEmpty() =
        runTest(mainDispatcherRule.dispatcher) {
            val viewModel = SectionsViewModel(FakeSportsRepository(), SavedStateHandle())
            advanceUntilIdle()

            viewModel.updateQuery("Несуществующая секция")

            val state = viewModel.uiState.value
            assertEquals(ScreenStatus.READY, state.status)
            assertTrue(state.sections.isEmpty())
            assertFalse(state.isSourceEmpty)
        }

    @Test
    fun clearingOrBlankingSearchRestoresAllSections() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeSportsRepository()
        val viewModel = SectionsViewModel(repository, SavedStateHandle())
        advanceUntilIdle()
        viewModel.updateQuery("Несуществующая секция")

        viewModel.updateQuery("")
        assertEquals(repository.sections.map { it.id }, viewModel.uiState.value.sections.map {
            it.section.id
        })

        viewModel.updateQuery("   ")
        assertEquals(repository.sections.size, viewModel.uiState.value.sections.size)
        assertFalse(viewModel.uiState.value.isSourceEmpty)
    }

    @Test
    fun searchMatchesSectionLocation() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = SectionsViewModel(FakeSportsRepository(), SavedStateHandle())
        advanceUntilIdle()

        viewModel.updateQuery("просвещения")

        assertEquals("judo-north", viewModel.uiState.value.sections.single().section.id)
    }

    @Test
    fun emptyRepositoryProducesSourceEmptyState() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = SectionsViewModel(FakeSportsRepository(emptyList()), SavedStateHandle())

        advanceUntilIdle()

        assertEquals(ScreenStatus.READY, viewModel.uiState.value.status)
        assertTrue(viewModel.uiState.value.sections.isEmpty())
        assertTrue(viewModel.uiState.value.isSourceEmpty)
    }

    @Test
    fun queryAndSelectedSectionSurviveViewModelRecreationWithSavedState() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeSportsRepository()
            val savedState = SavedStateHandle()
            val viewModel = SectionsViewModel(repository, savedState)
            advanceUntilIdle()

            viewModel.updateQuery("Бокс")
            viewModel.selectSection("boxing-center")
            val restored = SectionsViewModel(repository, savedState)
            advanceUntilIdle()

            assertEquals("Бокс", savedState.get<String>("searchQuery"))
            assertEquals("boxing-center", savedState.get<String>("selectedSectionId"))
            assertEquals("Бокс", restored.uiState.value.query)
            assertEquals("boxing-center", restored.uiState.value.selectedSectionId)
            assertEquals("boxing-center", restored.uiState.value.sections.single().section.id)
        }

    @Test
    fun preservesLoadErrorAndReloadsWithoutLosingQueryAfterRecovery() =
        runTest(mainDispatcherRule.dispatcher) {
            val error = IOException("Demo data is unavailable")
            val repository = FakeSportsRepository(failure = error)
            val savedState = SavedStateHandle(mapOf("searchQuery" to "Бокс"))
            val viewModel = SectionsViewModel(repository, savedState)
            advanceUntilIdle()

            assertEquals(ScreenStatus.ERROR, viewModel.uiState.value.status)
            assertSame(error, viewModel.uiState.value.error)

            repository.failure = null
            viewModel.reload()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(ScreenStatus.READY, state.status)
            assertNull(state.error)
            assertEquals("Бокс", state.query)
            assertEquals("boxing-center", state.sections.single().section.id)
        }
}
