package com.an.notesapp.composetexteditor.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.an.notesapp.composetexteditor.editor.FormattingAction


fun applyFormattingToSelection(
    annotatedString: AnnotatedString,
    selection: TextRange,
    activeFormatting: Set<FormattingAction>
): AnnotatedString {
    val builder = AnnotatedString.Builder(annotatedString)
    val spanStyle = getSpanStyle(activeFormatting)

    builder.addStyle(spanStyle, selection.start, selection.end)
    return builder.toAnnotatedString()
}

fun applyFormattingToNewText(
    annotatedString: AnnotatedString,
    addedText: String,
    startIndex: Int,
    activeFormatting: Set<FormattingAction>
): AnnotatedString {
    val builder = AnnotatedString.Builder(annotatedString)
    val spanStyle = getSpanStyle(activeFormatting)

    builder.append(addedText)
    builder.addStyle(spanStyle, startIndex, startIndex + addedText.length)
    return builder.toAnnotatedString()
}

fun applyFormattingToTextRemoval(
    annotatedString: AnnotatedString,
    newText: String
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    builder.append(newText)

    // Copy styles from the original AnnotatedString
    annotatedString.spanStyles.forEach { span ->
        if (span.start < newText.length) {
            builder.addStyle(
                style = span.item,
                start = span.start,
                end = minOf(span.end, newText.length) // Adjust for the new text length
            )
        }
    }

    return builder.toAnnotatedString()
}

fun applyListFormatting(
    annotatedString: AnnotatedString,
    cursorPosition: Int,
    activeFormatting: Set<FormattingAction>
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val text = annotatedString.text

    var currentLineStart = 0
    var isCursorAtLineEnd = false
    var nextLinePrefix = ""

    text.split("\n").forEachIndexed { index, line ->
        val currentLineEnd = currentLineStart + line.length
        val isCursorInLine = cursorPosition in currentLineStart..currentLineEnd

        // Determine prefix for the current line
        val prefix = when {
            FormattingAction.BulletList in activeFormatting -> if (isCursorInLine) "• " else ""
            FormattingAction.NumberedList in activeFormatting -> if (isCursorInLine) "${index + 1}. " else ""
            else -> ""
        }

        // Append the prefix and line content
        builder.append(prefix)
        builder.append(line)

        // Preserve spans for the current line
        annotatedString.spanStyles.forEach { span ->
            if (span.start < currentLineEnd && span.end > currentLineStart) {
                builder.addStyle(
                    span.item,
                    maxOf(span.start, currentLineStart) + prefix.length,
                    minOf(span.end, currentLineEnd) + prefix.length
                )
            }
        }

        // If the cursor is at the end of this line, prepare the next line's prefix
        if (isCursorInLine && cursorPosition == currentLineEnd) {
            isCursorAtLineEnd = true
            nextLinePrefix = when {
                FormattingAction.BulletList in activeFormatting -> "• "
                FormattingAction.NumberedList in activeFormatting -> "${index + 2}. "
                else -> ""
            }
        } else {
            isCursorAtLineEnd = false
        }

        // Add newline for all but the last line
        if (index < text.split("\n").lastIndex || isCursorAtLineEnd) {
            builder.append("\n")
        }

        // Append the new line prefix only once
        if (isCursorAtLineEnd && nextLinePrefix.isNotEmpty()) {
            builder.append(nextLinePrefix)
            isCursorAtLineEnd = false
        }

        currentLineStart = currentLineEnd + 1 // Update start for the next line
    }

    return builder.toAnnotatedString()
}


fun applyDynamicListFormatting(
    newText: String,
    annotatedString: AnnotatedString,
    cursorPosition: Int,
    activeFormatting: Set<FormattingAction>
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val lines = newText.split("\n")
    var currentLineStart = 0

    lines.forEachIndexed { index, line ->
        val isCursorInLine = cursorPosition in currentLineStart..(currentLineStart + line.length)

        // Determine the prefix
        val prefix = when {
            isCursorInLine && FormattingAction.BulletList in activeFormatting -> "• "
            isCursorInLine && FormattingAction.NumberedList in activeFormatting -> "${index + 1}. "
            else -> ""
        }

        if (isCursorInLine) {
            builder.append(prefix)
        }
        builder.append(line)

        // Copy spans and adjust for prefix
        annotatedString.spanStyles.forEach { span ->
            if (span.start < currentLineStart + line.length && span.end > currentLineStart) {
                builder.addStyle(
                    span.item,
                    maxOf(span.start, currentLineStart) + prefix.length,
                    minOf(span.end, currentLineStart + line.length) + prefix.length
                )
            }
        }

        if (index < lines.size - 1) {
            builder.append("\n")
        }

        currentLineStart += line.length + 1
    }

    return builder.toAnnotatedString()
}


fun getSpanStyle(activeFormatting: Set<FormattingAction>): SpanStyle {
    return SpanStyle(
        fontWeight = if (FormattingAction.Bold in activeFormatting) FontWeight.Bold else null,
        fontStyle = if (FormattingAction.Italic in activeFormatting) FontStyle.Italic else null,
        textDecoration = when {
            FormattingAction.Underline in activeFormatting -> TextDecoration.Underline
            FormattingAction.Strikethrough in activeFormatting -> TextDecoration.LineThrough
            else -> null
        },
        background = if (FormattingAction.Highlight in activeFormatting) Color.Yellow else Color.Transparent,
        fontSize = when {
            FormattingAction.Heading in activeFormatting -> 24.sp // Example font size for Heading
            FormattingAction.Subheading in activeFormatting -> 20.sp // Example font size for Subheading
            else -> 16.sp // Default font size for body text
        }
    )
}


