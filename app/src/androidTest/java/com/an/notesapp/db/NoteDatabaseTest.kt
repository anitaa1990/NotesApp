package com.an.notesapp.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.OffsetDateTime
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest: TestCase() {
    // get reference to the NoteDatabase and NoteDao class
    private lateinit var db: NoteDatabase
    private lateinit var dao: NoteDao

    // Override function setUp() and annotate it with @Before.
    // The @Before annotation makes sure fun setupDatabase() is executed before each class.
    // The function then creates a database using Room.inMemoryDatabaseBuilder which creates
    // a database in RAM instead of the persistence storage. This means the database will be
    // cleared once the process is killed.
    @Before
    public override fun setUp() {
        // get context -- since this is an instrumental test it requires
        // context from the running application
        // initialize the db and dao variable
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java)
            .build()
        dao = db.noteDao()
    }

    // Override function closeDb() and annotate it with @After.
    // @After annotation means our closing function will be called every-time after
    // executing test cases. This function will be called at last when this test class is called.
    @After
    fun closeDb() {
        db.close()
    }

    // create a test function and annotate it with @Test
    // here we are first adding an item to the db and then checking if that item
    // is present in the db -- if the item is present then our test cases pass
    @Test
    fun insertNote_returnsTrue() = runBlocking {
        val note = Note(
            id = 1,
            title = "Test",
            description = "Testing new note",
            encrypt = false,
            password = "",
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        )
        dao.insertNote(note)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            dao.fetchAllNotes().collect {
                assertEquals(it.first(), note)
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun updateNote_returnsTrue() = runBlocking {
        //insert note
        val note = Note(
            id = 1,
            title = "Test",
            description = "Testing new note",
            encrypt = false,
            password = "",
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        )
        dao.insertNote(note)

        // create updated note
        val updatedNote = note.copy(
            title = "Test 2",
            description = "Testing updating note",
            modifiedAt = OffsetDateTime.now()
        )

        // update
        dao.updateNote(updatedNote)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            // get note and assert if it equals to updated word
            dao.getNote(updatedNote.id).collect {
                assertEquals(it, updatedNote)
                assertNotSame(it, note)

                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun delete_returnsTrue() = runBlocking {
        val note1 = Note(
            id = 3,
            title = "Test",
            description = "Testing new note",
            encrypt = false,
            password = "",
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        )
        val note2 = Note(
            id = 4,
            title = "Test2",
            description = "Testing new note 2",
            encrypt = true,
            password = "anitaa1990",
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        )

        dao.insertNote(note1)
        dao.insertNote(note2)

        dao.deleteNote(note1)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            dao.fetchAllNotes().collect {
                assertEquals(it.size, 1)
                assertEquals(it.first(), note2)
                assertNotSame(it.first(), note1)
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()

    }
}