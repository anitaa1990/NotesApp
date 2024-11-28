package com.an.notesapp.composetexteditor.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.StrikethroughS
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.an.notesapp.composetexteditor.editor.FormattingAction

@Composable
fun EditorToolbar(
    activeFormatting: Set<FormattingAction>,
    onActionClick: (FormattingAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Heading Button
        FormatButton(
            action = FormattingAction.Heading,
            isActive = activeFormatting.contains(FormattingAction.Heading),
            onClick = {
                if (activeFormatting.contains(FormattingAction.Heading)) {
                    // Deselect Heading if already selected
                    onActionClick(FormattingAction.Heading)
                } else {
                    // Select Heading and ensure Subheading is deselected
                    onActionClick(FormattingAction.Subheading) // Deselect Subheading
                    onActionClick(FormattingAction.Heading)   // Select Heading
                }
            },
            icon = Icons.Filled.Title,
            contentDescription = "Heading"
        )

        // Subheading Button
        FormatButton(
            action = FormattingAction.Subheading,
            isActive = activeFormatting.contains(FormattingAction.Subheading),
            onClick = {
                if (activeFormatting.contains(FormattingAction.Subheading)) {
                    // Deselect Subheading if already selected
                    onActionClick(FormattingAction.Subheading)
                } else {
                    // Select Subheading and ensure Heading is deselected
                    onActionClick(FormattingAction.Heading) // Deselect Heading
                    onActionClick(FormattingAction.Subheading) // Select Subheading
                }
            },
            icon = Icons.Filled.Subtitles,
            contentDescription = "Subheading"
        )

        // Bold
        FormatButton(
            action = FormattingAction.Bold,
            isActive = activeFormatting.contains(FormattingAction.Bold),
            onClick = onActionClick,
            icon = Icons.Filled.FormatBold,
            contentDescription = "Bold"
        )

        // Italic
        FormatButton(
            action = FormattingAction.Italic,
            isActive = activeFormatting.contains(FormattingAction.Italic),
            onClick = onActionClick,
            icon = Icons.Filled.FormatItalic,
            contentDescription = "Italic"
        )

        // Underline
        FormatButton(
            action = FormattingAction.Underline,
            isActive = activeFormatting.contains(FormattingAction.Underline),
            onClick = onActionClick,
            icon = Icons.Filled.FormatUnderlined,
            contentDescription = "Underline"
        )

        // Strikethrough
        FormatButton(
            action = FormattingAction.Strikethrough,
            isActive = activeFormatting.contains(FormattingAction.Strikethrough),
            onClick = onActionClick,
            icon = Icons.Filled.StrikethroughS,
            contentDescription = "Strikethrough"
        )

        // Highlight
        FormatButton(
            action = FormattingAction.Highlight,
            isActive = activeFormatting.contains(FormattingAction.Highlight),
            onClick = onActionClick,
            icon = Icons.Filled.Highlight,
            contentDescription = "Highlight"
        )
    }
}

@Composable
fun FormatButton(
    action: FormattingAction,
    isActive: Boolean,
    onClick: (FormattingAction) -> Unit,
    icon: ImageVector,
    contentDescription: String,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = Color.Gray
) {
    IconButton(
        onClick = { onClick(action) },
        modifier = Modifier
            .background(
                color = if (isActive) activeColor.copy(alpha = 0.1f) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) activeColor else inactiveColor
        )
    }
}

fun handleActionClick(
    action: FormattingAction,
    activeFormatting: Set<FormattingAction>
): Set<FormattingAction> {
    return when (action) {
        FormattingAction.Heading -> {
            if (activeFormatting.contains(FormattingAction.Heading)) {
                // Deselect Heading
                activeFormatting - FormattingAction.Heading
            } else {
                // Select Heading and deselect Subheading
                activeFormatting - FormattingAction.Subheading + FormattingAction.Heading
            }
        }
        FormattingAction.Subheading -> {
            if (activeFormatting.contains(FormattingAction.Subheading)) {
                // Deselect Subheading
                activeFormatting - FormattingAction.Subheading
            } else {
                // Select Subheading and deselect Heading
                activeFormatting - FormattingAction.Heading + FormattingAction.Subheading
            }
        }
        else -> {
            // For other formatting options, toggle their state
            if (activeFormatting.contains(action)) {
                activeFormatting - action
            } else {
                activeFormatting + action
            }
        }
    }
}

