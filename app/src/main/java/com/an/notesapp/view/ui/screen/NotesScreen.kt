package com.an.notesapp.view.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.notesapp.R
import com.an.notesapp.intent.NoteIntent
import com.an.notesapp.model.db.Note
import com.an.notesapp.util.getDate
import com.an.notesapp.util.getTime
import com.an.notesapp.view.ui.component.EmptyScreen
import com.an.notesapp.view.ui.component.LoadingItem
import com.an.notesapp.view.ui.component.PasswordBottomSheet
import com.an.notesapp.view.ui.component.ProvideAppBarTitle
import com.an.notesapp.view.ui.theme.noteTextStyle
import com.an.notesapp.view.ui.theme.noteTitleStyle
import com.an.notesapp.view.ui.viewmodel.NoteViewModel

@Composable
fun NotesScreen(
    viewModel: NoteViewModel
) {
    val noteUiState = viewModel.notesViewState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

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

    if(noteUiState.value.isLoading) {
        // Show loading
        LoadingItem()

    } else if (noteUiState.value.notes.isEmpty()) {
        // show empty screen
        EmptyScreen()

    } else {
        // Show notes screen
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            columns = StaggeredGridCells.Adaptive(minSize = 140.dp),
        ) {
            val notes = noteUiState.value.notes
            items(notes.size) {
                val note = notes[it]
                NoteItem(
                    note = note,
                    onNoteItemClicked = { viewModel.handleIntent(NoteIntent.OpenNoteClicked(it)) },
                    onNoteItemDeleted = { viewModel.handleIntent(NoteIntent.DeleteNote(it)) }
                )
            }
        }
    }

    if (noteUiState.value.showPasswordSheet) {
        PasswordBottomSheet(
            isNoteLocked = true,
            errorMessageId = noteUiState.value.passwordErrorResId,
            onDismissRequest = { noteUiState.value.copy(showPasswordSheet = false) },
            onDoneRequest = {
                viewModel.handleIntent(NoteIntent.ValidatePassword(it))
            }
        )
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNoteItemClicked: (note: Note) -> Unit,
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
                .clickable { onNoteItemClicked(note) },
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
                .padding(start = 10.dp, bottom = 5.dp)
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
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .blur(blur),
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