package com.an.notesapp.view.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.notesapp.R
import com.an.notesapp.intent.NoteIntent
import com.an.notesapp.view.ui.component.PasswordBottomSheet
import com.an.notesapp.view.ui.component.ProvideAppBarAction
import com.an.notesapp.view.ui.component.ProvideAppBarTitle
import com.an.notesapp.view.ui.theme.noteTextStyle
import com.an.notesapp.view.ui.viewmodel.NoteDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(viewModel: NoteDetailViewModel) {
    val noteDetailViewState = viewModel.noteDetailViewState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    ProvideAppBarTitle {
        // Note title
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            value = noteDetailViewState.value.note.title,
            onValueChange = { viewModel.handleIntent(NoteIntent.UpdateNoteTitle(it)) },
            placeholder = { Text(stringResource(id = R.string.add_note_title)) },
            textStyle = MaterialTheme.typography.displaySmall,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

    // Bottom sheet for password
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        PasswordBottomSheet(
            isNoteLocked = noteDetailViewState.value.note.encrypt,
            onDismissRequest = { showBottomSheet = false },
            onDoneRequest = {
                viewModel.handleIntent(NoteIntent.LockNote(it))
                showBottomSheet = false
            }
        )
    }

    // Toolbar action buttons
    ProvideAppBarAction {
        // Lock button
        val resourceId = if (noteDetailViewState.value.note.encrypt) {
            R.drawable.ic_lock
        } else R.drawable.ic_lock_open

        IconToggleButton(
            checked = noteDetailViewState.value.note.encrypt,
            onCheckedChange = {
                if (noteDetailViewState.value.note.encrypt) viewModel.handleIntent(NoteIntent.UnLockNote)
                else showBottomSheet = true
            }
        ) {
            Icon(
                painter = painterResource(resourceId),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Delete button
        if (noteDetailViewState.value.showDeleteIcon) {
            IconButton(
                onClick = { viewModel.handleIntent(NoteIntent.DeleteNote(noteDetailViewState.value.note)) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Update/add button
        if (noteDetailViewState.value.showSaveIcon) {
            IconButton(onClick = { viewModel.handleIntent(NoteIntent.AddOrSaveNote) }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Note description
    Box {
        Card (
            modifier = Modifier.padding(15.dp),
            shape = RectangleShape,
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // Note description
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 180.dp),
                    value = noteDetailViewState.value.note.description,
                    onValueChange = { viewModel.handleIntent(NoteIntent.UpdateNoteDescription(it)) },
                    placeholder = { Text(stringResource(id = R.string.add_note_desc)) },
                    textStyle = noteTextStyle,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
