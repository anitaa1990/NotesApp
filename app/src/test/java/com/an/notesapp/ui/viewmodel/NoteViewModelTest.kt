package com.an.notesapp.ui.viewmodel

import app.cash.turbine.test
import com.an.notesapp.BaseUnitTest
import com.an.notesapp.intent.NoteIntent
import com.an.notesapp.model.db.Note
import com.an.notesapp.model.repository.NoteRepository
import com.an.notesapp.util.hashedString
import com.an.notesapp.view.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.OffsetDateTime

class NoteViewModelTest: BaseUnitTest() {
    private val repository: NoteRepository = mock()

    private val expectedNotes = listOf(
        Note(
            title = "Test 1",
            description = "",
            encrypt = false,
            password = null,
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        ),
        Note(
            title = "Test 2",
            description = "Test 2",
            encrypt = true,
            password = "anitaa".hashedString(),
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        )
    )

    @Test
    fun `when viewmodel is first initialized then returns initial state`() = runTest {
        val viewModel = NoteViewModel(repository)

        viewModel.notesViewState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertEquals(0, initialState.notes.size)
            assertEquals("", initialState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when db has notes available returns list of notes`() = runTest {
        `when`(repository.getNotes()).thenReturn(expectedNotes)
        val viewModel = NoteViewModel(repository)

        viewModel.notesViewState.test {
            // initial state
            awaitItem()

            viewModel.handleIntent(NoteIntent.LoadNotes)
            // loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals(0, loadingState.notes.size)
            assertEquals("", loadingState.errorMessage)

            // notes are loaded successfully
            val finalState = awaitItem()
            assertEquals(expectedNotes.size, finalState.notes.size)
            assertEquals(expectedNotes[0], finalState.notes.first())
            assertEquals(expectedNotes[1], finalState.notes.last())
            assertFalse(finalState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when db has no notes then returns empty list`() = runTest {
        `when`(repository.getNotes()).thenReturn(emptyList())

        val viewModel = NoteViewModel(repository)

        viewModel.notesViewState.test {
            // initial state
            awaitItem()

            viewModel.handleIntent(NoteIntent.LoadNotes)
            // loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals(0, loadingState.notes.size)
            assertEquals("", loadingState.errorMessage)

            // notes are loaded successfully but is empty
            val finalState = awaitItem()
            assertTrue(finalState.notes.isEmpty())
            assertFalse(finalState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }
}