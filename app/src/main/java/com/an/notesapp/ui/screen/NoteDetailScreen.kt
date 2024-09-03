package com.an.notesapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.notesapp.R
import com.an.notesapp.ui.viewmodel.NoteDetailViewModel

@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onCloseIconClicked: (noteId: Long) -> Unit
) {
    ProvideAppBarTitle { Text(stringResource(id = R.string.add_note_title)) }
    ProvideAppBarAction {
        TextButton(onClick = { /*TODO*/ }) {
            Text(text = stringResource(id = R.string.done_button))
        }
    }

    val note = viewModel.note.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    Box(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            // Note title
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                value = note.value?.title ?: "",
                onValueChange = { viewModel.updateNoteTitle(it) },
                placeholder = { Text(stringResource(id = R.string.add_note_title)) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 22.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            // Note description
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 180.dp),
                value = note.value?.description ?: "",
                onValueChange = { viewModel.updateNoteDesc(it) },
                placeholder = { Text(stringResource(id = R.string.add_note_desc)) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 18.sp
                ),
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
}