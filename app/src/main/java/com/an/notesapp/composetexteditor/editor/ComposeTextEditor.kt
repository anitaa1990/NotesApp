package com.an.notesapp.composetexteditor.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.an.notesapp.composetexteditor.utils.applyDynamicListFormatting
import com.an.notesapp.composetexteditor.utils.applyFormattingToNewText
import com.an.notesapp.composetexteditor.utils.applyFormattingToSelection
import com.an.notesapp.composetexteditor.utils.applyFormattingToTextRemoval
import com.an.notesapp.composetexteditor.utils.applyListFormatting

@Composable
fun RichTextEditor(
    editorState: EditorState,
    onStateChange: (EditorState) -> Unit,
    activeFormatting: Set<FormattingAction>,
    modifier: Modifier = Modifier
) {
    var previousTextFieldValue by remember { mutableStateOf(editorState.textFieldValue) }

    BasicTextField(
        value = editorState.textFieldValue,
        onValueChange = { newValue ->
            val oldText = previousTextFieldValue.text
            val newText = newValue.text
            val cursorPosition = newValue.selection.start

            val isTextAdded = newText.length > oldText.length
            val isTextRemoved = newText.length < oldText.length
            val isNewLineAdded = isTextAdded && newText.lastOrNull() == '\n'

            val updatedAnnotatedString = when {
                // Handle new lines for Bullet/Numbered Lists
                (FormattingAction.BulletList in activeFormatting || FormattingAction.NumberedList in activeFormatting) && isNewLineAdded -> {
                    applyListFormatting(editorState.annotatedString, cursorPosition, activeFormatting)
                }
                // Dynamically add prefixes when typing starts
                (FormattingAction.BulletList in activeFormatting || FormattingAction.NumberedList in activeFormatting) -> {
                    applyDynamicListFormatting(newText, editorState.annotatedString, cursorPosition, activeFormatting)
                }
                // Handle new text added with active formatting
                isTextAdded -> {
                    val addedText = newText.substring(oldText.length)
                    applyFormattingToNewText(editorState.annotatedString, addedText, oldText.length, activeFormatting)
                }
                // Handle formatting for selected text
                !newValue.selection.collapsed -> {
                    applyFormattingToSelection(editorState.annotatedString, newValue.selection, activeFormatting)
                }
                // Handle text removal
                isTextRemoved -> {
                    applyFormattingToTextRemoval(editorState.annotatedString, newText)
                }
                // Handle Heading/Subheading at the cursor
                FormattingAction.Heading in activeFormatting -> {
                    applyFormattingToSelection(
                        annotatedString = editorState.annotatedString,
                        selection = TextRange(cursorPosition, cursorPosition),
                        activeFormatting = setOf(FormattingAction.Heading)
                    )
                }
                FormattingAction.Subheading in activeFormatting -> {
                    applyFormattingToSelection(
                        annotatedString = editorState.annotatedString,
                        selection = TextRange(cursorPosition, cursorPosition),
                        activeFormatting = setOf(FormattingAction.Subheading)
                    )
                }
                else -> {
                    // Pass-through: no significant change
                    editorState.annotatedString
                }
            }

            previousTextFieldValue = newValue

            onStateChange(
                editorState.copy(
                    textFieldValue = newValue,
                    annotatedString = updatedAnnotatedString
                )
            )
        },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground, // Ensure contrasting color
            fontSize = 16.sp
        ),
        modifier = modifier,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Text(
                text = editorState.annotatedString,
                modifier = Modifier.padding(8.dp)
            )
        }
    )
}


