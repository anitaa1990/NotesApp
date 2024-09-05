package com.an.notesapp.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.notesapp.R
import com.an.notesapp.ui.component.ProvideAppBarAction
import com.an.notesapp.ui.component.ProvideAppBarTitle
import com.an.notesapp.ui.theme.noteTextStyle
import com.an.notesapp.ui.theme.noteTitleStyle
import com.an.notesapp.ui.viewmodel.NoteDetailViewModel

@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel
) {
    val noteUiState = viewModel.noteUiState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    // Toolbar title
    ProvideAppBarTitle {
        Text(
            text = stringResource(id = noteUiState.value.toolbarTitle),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    // Toolbar action button: Done
    ProvideAppBarAction {
        TextButton( onClick = viewModel::addOrUpdateNote ) {
            Text(
                text = stringResource(id = R.string.done_button),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
    ) {
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
                // Note title
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    value = noteUiState.value.note.title,
                    onValueChange = { viewModel.updateNoteTitle(it) },
                    placeholder = { Text(stringResource(id = R.string.add_note_title)) },
                    textStyle = noteTitleStyle,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
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
                    value = noteUiState.value.note.description,
                    onValueChange = { viewModel.updateNoteDesc(it) },
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