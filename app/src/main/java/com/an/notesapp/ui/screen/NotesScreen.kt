package com.an.notesapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.notesapp.R
import com.an.notesapp.db.Note
import com.an.notesapp.ui.component.ProvideAppBarTitle
import com.an.notesapp.ui.viewmodel.NoteViewModel
import com.an.notesapp.util.getDate
import com.an.notesapp.util.getTime
import java.time.OffsetDateTime

@Composable
fun NotesScreen(
    viewModel: NoteViewModel,
    onNoteItemClicked: (noteId: Long) -> Unit
) {
    // Toolbar title
    ProvideAppBarTitle {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp),
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    val notes = viewModel.notes.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    if (notes.value.isEmpty()) {
        EmptyScreen()
    } else {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(top = 30.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            columns = StaggeredGridCells.Adaptive(minSize = 140.dp),
        ) {
            items(notes.value.size) {
                val note = notes.value[it]
                NoteItem(
                    note = note,
                    onNoteItemClicked = onNoteItemClicked,
                    onNoteItemDeleted = { viewModel.deleteNote(note) }
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNoteItemClicked: (noteId: Long) -> Unit,
    onNoteItemDeleted: (note: Note) -> Unit
) {
    Box(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        Card (
            modifier = Modifier
                .padding(bottom = 12.dp)
                .clickable { onNoteItemClicked(note.id) },
            shape = RectangleShape,
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column (modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, top = 6.dp, bottom = 6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = note.title,
                        modifier = Modifier
                            .wrapContentWidth()
                            .weight(
                                weight = 1.0f,
                                fill = false,
                            ),
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    IconButton(
                        onClick = { onNoteItemDeleted(note) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = note.description,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp, end = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = String.format(
                        stringResource(id = R.string.note_list_date),
                        note.createdAt.getDate(), note.createdAt.getTime()
                    ),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, end = 8.dp),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(14.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = Icons.Default.Create,
                contentDescription =""
            )
            Text(
                text = stringResource(id = R.string.note_list_empty),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun NoteItemPreview() {
    NoteItem(
        note = Note(
            id = 1L,
            title = "Test 2",
            description = "Testing new note",
            encrypt = true,
            password = null,
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        ),
        { },
        { }
    )
}

@Preview
@Composable
fun EmptyScreenPreview() {
    EmptyScreen()
}
