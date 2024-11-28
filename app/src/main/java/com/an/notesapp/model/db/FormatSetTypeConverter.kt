package com.an.notesapp.model.db

import androidx.room.TypeConverter
import com.an.notesapp.composetexteditor.editor.FormattingAction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FormatSetTypeConverter {
    @TypeConverter
    fun fromString(value: String): Set<FormattingAction>? {
        val setType = object : TypeToken<Set<FormattingAction>?>() {}.type
        return Gson().fromJson(value, setType)
    }

    @TypeConverter
    fun fromSet(formattingActions: Set<FormattingAction>?): String? {
        return Gson().toJson(formattingActions)
    }
}
