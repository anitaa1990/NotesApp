package com.an.notesapp.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.notesapp.R
import com.an.notesapp.ui.component.PasswordBottomSheet
import com.an.notesapp.ui.component.ProvideAppBarAction
import com.an.notesapp.ui.component.ProvideAppBarTitle
import com.an.notesapp.ui.model.NoteAction
import com.an.notesapp.ui.theme.noteTextStyle
import com.an.notesapp.ui.theme.noteTitleStyle
import com.an.notesapp.ui.viewmodel.NoteDetailViewModel

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel
) {
    val noteUiState = viewModel.noteUiModelState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    ProvideAppBarTitle {
        // Note title
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            value = noteUiState.value.title,
            onValueChange = { viewModel.updateNoteTitle(it) },
            placeholder = { Text(stringResource(id = R.string.add_note_title)) },
            textStyle = noteTitleStyle,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

    val showBottomSheet = remember { mutableStateOf(false) }
    val errorResId = remember { mutableStateOf<Int?>(null) }

    // Toolbar action buttons
    ProvideAppBarAction {
        // Lock note button
        LockNoteToggleButton(
            isNoteLocked = noteUiState.value.noteLocked,
            onCheckedChange = { showBottomSheet.value = true }
        )

        // Set reminder button
        IconButton( onClick = viewModel::addOrUpdateNote ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Update/add button
        IconButton( onClick = viewModel::addOrUpdateNote ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

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
                    value = noteUiState.value.description,
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

    when(val noteActionState = viewModel.noteActionState.collectAsState().value) {
        is NoteAction.NoteLocked, is NoteAction.NoteNotLocked -> { showBottomSheet.value = false }
        is NoteAction.PasswordValidationError -> { errorResId.value = noteActionState.errorResId }
        is NoteAction.PasswordValidationSuccess, is NoteAction.Idle -> { }
    }

    if (showBottomSheet.value) {
        PasswordBottomSheet(
            isNoteLocked = noteUiState.value.noteLocked,
            errorMessageId = errorResId.value,
            onDismissRequest = { showBottomSheet.value = false },
            onDoneRequest = { viewModel.validatePassword(!noteUiState.value.noteLocked, it) }
        )
    }
}

@Composable
fun LockNoteToggleButton(
    isNoteLocked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val resourceId = if (isNoteLocked) R.drawable.ic_lock else R.drawable.ic_lock_open

    IconToggleButton(
        checked = isNoteLocked,
        onCheckedChange = onCheckedChange
    ) {
        Icon(
            painter = painterResource(resourceId),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}