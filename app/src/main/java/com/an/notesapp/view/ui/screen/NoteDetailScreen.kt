package com.an.notesapp.view.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.notesapp.view.ui.component.ProvideAppBarTitle
import com.an.notesapp.R
import com.an.notesapp.model.db.Note
import com.an.notesapp.view.ui.theme.noteTextStyle
import com.an.notesapp.view.ui.viewmodel.NoteDetailViewModel

@Composable
fun NoteDetailScreen(viewModel: NoteDetailViewModel) {
    val noteDetailViewState = viewModel.noteDetailViewState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    ).value

    NoteTitle(noteDetailViewState.note)
    NoteDescription(noteDetailViewState.note)
}

@Composable
private fun NoteTitle(note: Note) {
    ProvideAppBarTitle {
        // Note title
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            value = note.title,
            onValueChange = {  },
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
}

@Composable
private fun NoteDescription(note: Note) {
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
                    .fillMaxHeight(fraction = 0.9f)
            ) {
                // Note description
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 180.dp),
                    value = note.description,
                    onValueChange = {  },
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