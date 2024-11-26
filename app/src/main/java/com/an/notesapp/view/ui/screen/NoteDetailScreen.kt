package com.an.notesapp.view.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.an.notesapp.view.ui.component.ProvideAppBarTitle
import com.an.notesapp.R

@Composable
fun NoteDetailScreen() {
    ProvideAppBarTitle {
        // Note title
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            value = "New note 2",
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