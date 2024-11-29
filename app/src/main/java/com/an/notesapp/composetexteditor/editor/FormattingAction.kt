package com.an.notesapp.composetexteditor.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

enum class FormattingAction {
    Heading,
    SubHeading,
    Bold,
    Italics,
    Underline,
    Strikethrough,
    Highlight
}

val HighlightStyle = SpanStyle(background = Color.Yellow)
val HeadingStyle = SpanStyle(fontSize = 24.sp)
val SubtitleStyle = SpanStyle(fontSize = 20.sp)
val StrikeThroughStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
val UnderlineStyle = SpanStyle(textDecoration = TextDecoration.Underline)
val ItalicsStyle = SpanStyle(fontStyle = FontStyle.Italic)
val BoldStyle = SpanStyle(fontWeight = FontWeight.Bold)
val DefaultStyle = SpanStyle()
