package com.an.notesapp.ui.viewmodel

import com.an.notesapp.BaseUnitTest
import com.an.notesapp.db.Note
import com.an.notesapp.db.NoteDao
import com.an.notesapp.repository.NoteRepository
import com.an.notesapp.util.hashedString
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.OffsetDateTime

class NoteViewModelTest: BaseUnitTest() {
    private val dao: NoteDao = mock()
    private val repository: NoteRepository = NoteRepository(dao)

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
    fun `when db has notes available returns list of notes`() = runTest {
        val viewModel = ViewModelBuilder()
            .withLoadedData()
            .build()

        val notes = viewModel.notesUiState.value
        assertEquals(expectedNotes.size, notes.notes.size)
        assertEquals(expectedNotes[0], notes.notes.first())
        assertEquals(expectedNotes[1], notes.notes.last())
    }

    @Test
    fun `when db has no notes then returns empty list`() = runTest {
        val viewModel = ViewModelBuilder()
            .withEmptyData()
            .build()

        val notes = viewModel.notesUiState.value
        assertTrue(notes.isEmpty)
        assertTrue(notes.notes.isEmpty())
    }

    private inner class ViewModelBuilder {
        fun build() = NoteViewModel(repository)

        fun withEmptyData(): ViewModelBuilder {
            `when`(dao.fetchAllNotes()).thenReturn(
                flow { emit(emptyList()) }
            )
            return this
        }
        fun withLoadedData(): ViewModelBuilder {
            `when`(dao.fetchAllNotes()).thenReturn(
                flow { emit(expectedNotes) }
            )
            return this
        }
    }
}