package com.an.notesapp.composetexteditor.editor

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

@Composable
fun ComposeTextEditor(
    text: String,
    activeFormats: Set<FormattingAction>,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Track text and formatting spans
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }
    var formattingSpans by remember { mutableStateOf(listOf<FormattingSpan>()) }

    // Synchronize textFieldValue with the latest `text`
    LaunchedEffect(text) {
        if (text != textFieldValue.text) {
            textFieldValue = TextFieldValue(text)
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            // Detect text changes
            if (newValue.text.length > textFieldValue.text.length) {
                // Handle new text input
                val start = textFieldValue.text.length
                val end = newValue.text.length
                if (activeFormats.isNotEmpty()) {
                    // Add a new span for the newly added text
                    formattingSpans = formattingSpans + FormattingSpan(start, end, activeFormats.toSet())
                }
            }
            textFieldValue = newValue

            // Apply all spans to the current text
            val styledText = applyFormattingToText(newValue.text, formattingSpans)
            textFieldValue = newValue.copy(annotatedString = styledText)

            onTextChange(newValue.text)
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
private fun applyFormattingToText(
    text: String,
    formattingSpans: List<FormattingSpan>
): AnnotatedString {
    val builder = AnnotatedString.Builder(text)

    // Apply each formatting span
    formattingSpans.forEach { span ->
        span.formats.forEach { format ->
            when (format) {
                FormattingAction.Bold -> builder.addStyle(
                    style = SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Italics -> builder.addStyle(
                    style = SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
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
