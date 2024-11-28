package com.an.notesapp.model.db

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.room.TypeConverter
import com.google.gson.Gson

class AnnotatedStringConverter {

    @TypeConverter
    fun fromAnnotatedString(value: AnnotatedString): String {
        val spans = value.spanStyles.map {
            AnnotatedStringSpan(
                start = it.start,
                end = it.end,
                style = SpanStyleData(
                    fontWeight = it.item.fontWeight?.weight,
                    fontStyle = it.item.fontStyle?.toSerializedString(),
                    textDecoration = it.item.textDecoration?.toSerializedString()
                )
            )
        }
        return Gson().toJson(AnnotatedStringData(value.text, spans))
    }

    @TypeConverter
    fun toAnnotatedString(value: String): AnnotatedString {
        val annotatedStringData = Gson().fromJson(value, AnnotatedStringData::class.java)
        val spanStyles = annotatedStringData.spans.map {
            AnnotatedString.Range(
                item = SpanStyle(
                    fontWeight = it.style?.fontWeight?.let { weight -> androidx.compose.ui.text.font.FontWeight(weight) },
                    fontStyle = it.style?.fontStyle?.toFontStyle(),
                    textDecoration = it.style?.textDecoration?.toTextDecoration()
                ),
                start = it.start,
                end = it.end
            )
        }
        return AnnotatedString(annotatedStringData.text, spanStyles)
    }
}

/**
 * Utility functions for FontStyle serialization.
 */
fun FontStyle.toSerializedString(): String = when (this) {
    FontStyle.Normal -> "Normal"
    FontStyle.Italic -> "Italic"
    else -> "None" // Default to "None" for unsupported cases
}

fun String.toFontStyle(): FontStyle = when (this) {
    "Normal" -> FontStyle.Normal
    "Italic" -> FontStyle.Italic
    else -> FontStyle.Normal // Default to Normal
}

/**
 * Utility functions for TextDecoration serialization.
 */
fun TextDecoration.toSerializedString(): String = when (this) {
    TextDecoration.Underline -> "Underline"
    TextDecoration.LineThrough -> "LineThrough"
    else -> "None" // Default to "None" for unsupported cases
}

fun String.toTextDecoration(): TextDecoration? = when (this) {
    "Underline" -> TextDecoration.Underline
    "LineThrough" -> TextDecoration.LineThrough
    "None" -> null
    else -> null
}

// Data classes for JSON serialization
data class AnnotatedStringData(
    val text: String,
    val spans: List<AnnotatedStringSpan>
)

data class AnnotatedStringSpan(
    val start: Int,
    val end: Int,
    val style: SpanStyleData?
)

data class SpanStyleData(
    val fontWeight: Int?,
    val fontStyle: String?, // Store FontStyle as a string
    val textDecoration: String? // Store TextDecoration as a string
)
