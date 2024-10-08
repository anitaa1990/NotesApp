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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.notesapp.R
import com.an.notesapp.db.Note
import com.an.notesapp.ui.component.PasswordBottomSheet
import com.an.notesapp.ui.component.ProvideAppBarTitle
import com.an.notesapp.ui.model.NoteAction
import com.an.notesapp.ui.theme.noteTextStyle
import com.an.notesapp.ui.theme.noteTitleStyle
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
                .fillMaxWidth(),
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
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            columns = StaggeredGridCells.Adaptive(minSize = 140.dp),
        ) {
            items(notes.value.size) {
                val note = notes.value[it]
                NoteItem(
                    note = note,
                    onNoteItemClicked = { viewModel.onNoteItemClicked(note) },
                    onNoteItemDeleted = { viewModel.deleteNote(note) }
                )
            }
        }
    }

    val noteActionState = viewModel.noteActionState.collectAsState().value

    val showBottomSheet = remember { mutableStateOf(false) }
    val errorResId = remember { mutableStateOf<Int?>(null) }

    when(noteActionState) {
        is NoteAction.NoteLocked -> { showBottomSheet.value = true }
        is NoteAction.NoteNotLocked -> {
            viewModel.reset()
            onNoteItemClicked(noteActionState.noteId)
        }
        is NoteAction.PasswordValidationSuccess -> {
            showBottomSheet.value = false
            viewModel.reset()
            onNoteItemClicked(noteActionState.noteId)
        }
        is NoteAction.PasswordValidationError -> { errorResId.value = noteActionState.errorResId }
        is NoteAction.Idle -> {
            showBottomSheet.value = false
            errorResId.value = null
        }
    }

    if(showBottomSheet.value) {
        PasswordBottomSheet(
            isNoteLocked = true,
            errorMessageId = errorResId.value,
            onDismissRequest = { viewModel.reset() },
            onDoneRequest = { viewModel.validatePassword(it) }
        )
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNoteItemClicked: (noteId: Long) -> Unit,
    onNoteItemDeleted: (note: Note) -> Unit
) {
    val alpha = if (note.encrypt) 1f else 0f
    val blur = if (note.encrypt) 10.dp else 0.dp
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
            HorizontalDivider (
                color = MaterialTheme.colorScheme.primary,
                thickness = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha),
            )
            Column (modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = note.title,
                        style = noteTitleStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    IconButton(
                        modifier = Modifier.weight(0.25f),
                        onClick = { onNoteItemDeleted(note) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    style = noteTextStyle,
                    modifier = Modifier.padding(end = 10.dp).blur(blur),
                    text = note.description,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(
                            stringResource(id = R.string.note_list_date),
                            note.createdAt.getDate(), note.createdAt.getTime()
                        ),
                        modifier = Modifier.weight(1f),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(12.dp)
                            .weight(0.25f)
                            .alpha(alpha)
                    )
                }
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
                style = noteTitleStyle,
                color = MaterialTheme.colorScheme.onSecondaryContainer
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
            description = "Testing new note sdskd lskslk sflk sl sfs;lfj sfssdk aewiewidm sdkj s kjdskdj kjsd k sdhs idijsd i sdjsl ef elfj ",
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
