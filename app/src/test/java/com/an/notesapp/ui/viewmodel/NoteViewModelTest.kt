package com.an.notesapp.ui.viewmodel

import app.cash.turbine.test
import com.an.notesapp.BaseUnitTest
import com.an.notesapp.db.Note
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
    fun `when db has notes available returns list of notes`() = runTest {
        val viewModel = ViewModelBuilder()
            .withLoadedData()
            .build()

        viewModel.notes.test {
            val notes = awaitItem()
            assertEquals(expectedNotes.size, notes.size)
            assertEquals(expectedNotes[0], notes.first())
            assertEquals(expectedNotes[1], notes.last())
        }
    }

    @Test
    fun `when db has no notes then returns empty list`() = runTest {
        val viewModel = ViewModelBuilder()
            .withEmptyData()
            .build()

        val notes = viewModel.notes.value
        assertTrue(notes.isEmpty())
    }

    private inner class ViewModelBuilder {
        fun build() = NoteViewModel(repository)

        fun withEmptyData(): ViewModelBuilder {
            `when`(repository.getNotes()).thenReturn(
                flow { emit(emptyList()) }
            )
            return this
        }
        fun withLoadedData(): ViewModelBuilder {
            `when`(repository.getNotes()).thenReturn(
                flow { emit(expectedNotes) }
            )
            return this
        }
    }
}