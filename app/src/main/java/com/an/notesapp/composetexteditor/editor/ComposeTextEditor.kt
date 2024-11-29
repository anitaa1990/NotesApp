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
import androidx.compose.ui.unit.sp

@Composable
fun ComposeTextEditor(
    annotatedString: AnnotatedString,
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
            val newText = newValue.text

            // Adjust spans for the updated text
            val adjustedSpans = adjustSpansForNewText(
                existingAnnotatedString = textFieldValue.annotatedString,
                newText = newText
            )

            // Add active formatting spans for the selected range or newly typed text
            val updatedSpans = addActiveFormattingSpans(
                currentSpans = adjustedSpans,
                selectionStart = newValue.selection.start,
                selectionEnd = newValue.selection.end,
                activeFormats = activeFormats
            )

            // Create a new AnnotatedString with all spans applied
            val updatedAnnotatedString = applyFormattingSpans(newText, updatedSpans)

            // Update TextFieldValue
            textFieldValue = newValue.copy(annotatedString = updatedAnnotatedString)

            // Notify parent about the updates
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
                    style = BoldStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Italics -> builder.addStyle(
                    style = ItalicsStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Underline -> builder.addStyle(
                    style = UnderlineStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Strikethrough -> builder.addStyle(
                    style = StrikeThroughStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Highlight -> builder.addStyle(
                    style = HighlightStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Heading -> builder.addStyle(
                    style = HeadingStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.SubHeading -> builder.addStyle(
                    style = SubtitleStyle,
                    start = span.start,
                    end = span.end
                )
            }
        }
    }

    return builder.toAnnotatedString()
}

private fun adjustSpansForNewText(
    existingAnnotatedString: AnnotatedString,
    newText: String
): List<FormattingSpan> {
    val newTextLength = newText.length

    return existingAnnotatedString.spanStyles.mapNotNull { span ->
        // Adjust start and end indices to fit the new text
        val adjustedStart = span.start.coerceAtMost(newTextLength)
        val adjustedEnd = span.end.coerceAtMost(newTextLength)

        if (adjustedStart < adjustedEnd) {
            // Convert AnnotatedString.Range into a FormattingSpan
            FormattingSpan(
                start = adjustedStart,
                end = adjustedEnd,
                formats = extractFormatsFromSpanStyle(span.item)
            )
        } else {
            null // Remove invalid spans
        }
    }
}

private fun extractFormatsFromSpanStyle(spanStyle: SpanStyle): Set<FormattingAction> {
    val formats = mutableSetOf<FormattingAction>()

    if (spanStyle == BoldStyle) formats.add(FormattingAction.Bold)
    if (spanStyle == ItalicsStyle) formats.add(FormattingAction.Italics)
    if (spanStyle == UnderlineStyle) formats.add(FormattingAction.Underline)
    if (spanStyle == StrikeThroughStyle) formats.add(FormattingAction.Strikethrough)
    if (spanStyle == HighlightStyle) formats.add(FormattingAction.Highlight)
    if (spanStyle == HeadingStyle) formats.add(FormattingAction.Heading)
    if (spanStyle == SubtitleStyle) formats.add(FormattingAction.SubHeading)

    return formats
}

private fun addActiveFormattingSpans(
    currentSpans: List<FormattingSpan>,
    selectionStart: Int,
    selectionEnd: Int,
    activeFormats: Set<FormattingAction>
): List<FormattingSpan> {
    val updatedSpans = currentSpans.toMutableList()

    if (selectionStart != selectionEnd && activeFormats.isNotEmpty()) {
        // Add formatting for the selected range
        updatedSpans.add(
            FormattingSpan(selectionStart, selectionEnd, activeFormats)
        )
    } else if (activeFormats.isNotEmpty() && selectionStart > 0) {
        // Apply active formats to the last character typed
        val lastCharIndex = selectionStart - 1
        updatedSpans.add(
            FormattingSpan(lastCharIndex, selectionStart, activeFormats)
        )
    }

    return updatedSpans
}

