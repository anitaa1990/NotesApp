package com.an.notesapp.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.an.notesapp.R
import com.an.notesapp.ui.theme.noteTextStyle
import com.an.notesapp.ui.theme.noteTitleStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordBottomSheet(
    isNoteLocked: Boolean,
    errorMessageId: Int?,
    onDismissRequest: () -> Unit,
    onDoneRequest: (password: String) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val textResourceId = if (isNoteLocked) {
        R.string.unlock_password_note_title
    } else R.string.lock_password_note_title

    ModalBottomSheet(
        modifier = Modifier
            .padding(bottom = 40.dp)
            .fillMaxHeight(fraction = 0.6f),
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
        ) {
            Text(
                text = stringResource(id = textResourceId),
                style = noteTitleStyle
            )

            val visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else PasswordVisualTransformation()

            val description = if (passwordVisible) {
                R.string.label_password_invisible
            } else R.string.label_password_visible

            val imageResourceId = if (passwordVisible)
                R.drawable.ic_visibility_24
            else R.drawable.ic_visibility_off

            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(end = 20.dp, top = 20.dp, bottom = 20.dp),
                placeholder = { Text(text = stringResource(id = R.string.label_password)) },
                singleLine = true,
                visualTransformation = visualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton( onClick = {passwordVisible = !passwordVisible} ) {
                        Icon(
                            painter  = painterResource(id = imageResourceId),
                            contentDescription = stringResource(description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                supportingText = {
                    Text(
                        style = noteTextStyle,
                        color = Color.Red,
                        textAlign = TextAlign.Start,
                        text = errorMessageId?.let { stringResource(id = it) } ?: ""
                    )
                }
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .align(Alignment.CenterHorizontally),
                onClick = { onDoneRequest(password) },
                shape = CutCornerShape(10)
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    style = noteTextStyle,
                    text = stringResource(id = R.string.done_button)
                )
            }
        }
    }
}
