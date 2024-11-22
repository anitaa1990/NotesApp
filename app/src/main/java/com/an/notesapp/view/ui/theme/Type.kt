package com.an.notesapp.view.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val noteTitleStyle = TextStyle(
    lineHeight = 28.sp,
    letterSpacing = 0.sp,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    fontFamily = FontFamily.Serif
)

val noteTextStyle = TextStyle(
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
    fontSize = 14.sp,
    fontFamily = FontFamily.Serif,
    textAlign = TextAlign.Start
)