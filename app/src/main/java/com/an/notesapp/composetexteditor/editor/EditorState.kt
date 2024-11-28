package com.an.notesapp.composetexteditor.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue

data class EditorState(
    val textFieldValue: TextFieldValue = TextFieldValue(""),
    val annotatedString: AnnotatedString = AnnotatedString("")
)
