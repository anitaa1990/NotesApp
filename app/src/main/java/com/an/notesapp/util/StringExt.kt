package com.an.notesapp.util

import java.security.MessageDigest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun String?.hashedString() =
    this?.let {
        MessageDigest.getInstance("SHA-256")
            .digest(toByteArray())
            .fold(StringBuilder()) { sb, string -> sb.append("%02x".format(string)) }.toString()
    }

fun OffsetDateTime.getDate(): String {
    val newDateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    return this.format(newDateFormat)
}

fun OffsetDateTime.getTime(): String {
    val newDateFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
    return this.format(newDateFormat)
}
