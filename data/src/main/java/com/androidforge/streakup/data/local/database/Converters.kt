package com.androidforge.streakup.data.local.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.ZoneOffset

// This file was not explicitly in the architect output, but is required for LocalDate.
// Adding it here as a necessary utility for the data layer.

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}