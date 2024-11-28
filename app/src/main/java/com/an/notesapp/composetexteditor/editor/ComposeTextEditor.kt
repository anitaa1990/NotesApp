package com.an.notesapp.composetexteditor.editor

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

@Composable
fun ComposeTextEditor(
    annotatedString: AnnotatedString,
    formattingSpans: List<FormattingSpan>,
    activeFormats: Set<FormattingAction>,
    onAnnotatedStringChange: (AnnotatedString) -> Unit,
    onFormattingSpansChange: (List<FormattingSpan>) -> Unit,
    modifier: Modifier = Modifier
) {
    // Track text and formatting spans
    var textFieldValue by remember { mutableStateOf(TextFieldValue(annotatedString)) }

    // Synchronize textFieldValue with the latest `text`
    LaunchedEffect(annotatedString) {
        if (annotatedString != textFieldValue.annotatedString) {
            textFieldValue = TextFieldValue(annotatedString)
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val updatedSpans = updateFormattingSpans(
                textFieldValue = newValue,
                activeFormats = activeFormats,
                currentSpans = formattingSpans
            )

            val updatedAnnotatedString = applyFormattingSpans(newValue.text, updatedSpans)

            textFieldValue = newValue.copy(annotatedString = updatedAnnotatedString)

            // Notify parent about the updated AnnotatedString and spans
            onAnnotatedStringChange(updatedAnnotatedString)
            onFormattingSpansChange(updatedSpans)
        },
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
        cursorBrush = SolidColor(Color.Black),
        modifier = modifier
    )
}

/**
 * Represents a span of formatting applied to a specific range of text.
 */
data class FormattingSpan(val start: Int, val end: Int, val formats: Set<FormattingAction>)

/**
 * Applies formatting spans to the given text.
 */
private fun applyFormattingSpans(
    text: String,
    formattingSpans: List<FormattingSpan>
): AnnotatedString {
    val builder = AnnotatedString.Builder(text)

    // Reapply all formatting spans
    formattingSpans.forEach { span ->
        span.formats.forEach { format ->
            when (format) {
                FormattingAction.Bold -> builder.addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold),
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Italics -> builder.addStyle(
                    style = SpanStyle(fontStyle = FontStyle.Italic),
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Underline -> builder.addStyle(
                    style = SpanStyle(textDecoration = TextDecoration.Underline),
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Strikethrough -> builder.addStyle(
                    style = SpanStyle(textDecoration = TextDecoration.LineThrough),
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Highlight -> builder.addStyle(
                    style = SpanStyle(background = Color.Yellow),
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Heading -> builder.addStyle(
                    style = SpanStyle(fontSize = 24.sp),
                    start = span.start,
                    end = span.end
                )
                FormattingAction.SubHeading -> builder.addStyle(
                    style = SpanStyle(fontSize = 20.sp),
                    start = span.start,
                    end = span.end
                )
            }
        }
    }

    return builder.toAnnotatedString()
}

private fun updateFormattingSpans(
    textFieldValue: TextFieldValue,
    activeFormats: Set<FormattingAction>,
    currentSpans: List<FormattingSpan>
): List<FormattingSpan> {
    val selectionStart = textFieldValue.selection.start
    val selectionEnd = textFieldValue.selection.end

    // If no selection, apply to the last typed character
    val rangeStart = if (selectionStart == selectionEnd && selectionStart > 0) selectionStart - 1 else selectionStart
    val rangeEnd = selectionEnd

    val updatedSpans = currentSpans.toMutableList()

    // Add new formatting spans for the selected range
    if (rangeStart != rangeEnd) {
        val newSpan = FormattingSpan(rangeStart, rangeEnd, activeFormats.toSet())
        updatedSpans.add(newSpan)
    }

    return updatedSpans
}

