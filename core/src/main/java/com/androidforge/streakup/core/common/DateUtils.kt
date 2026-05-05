package com.androidforge.streakup.core.common

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun formatHourMinuteToTimeString(hour: Int, minute: Int): String {
        return LocalTime.of(hour, minute).format(timeFormatter)
    }

    fun parseTimeStringToHourMinute(timeString: String?): Pair<Int, Int> {
        return try {
            val time = LocalTime.parse(timeString, timeFormatter)
            Pair(time.hour, time.minute)
        } catch (e: Exception) {
            // Default to a reasonable time if parsing fails
            Pair(9, 0) // 9:00 AM
        }
    }

    fun getCurrentHourMinuteAsTimeString(): String {
        return LocalTime.now().format(timeFormatter)
    }

    fun isToday(date: LocalDate): Boolean {
        return date == LocalDate.now()
    }

    fun isYesterday(date: LocalDate): Boolean {
        return date == LocalDate.now().minusDays(1)
    }

    fun getDayOfWeekShortName(dayOfWeekInt: Int): String {
        // dayOfWeekInt: 0=Sunday, 1=Monday, ..., 6=Saturday
        val javaDayOfWeek = when (dayOfWeekInt) {
            0 -> DayOfWeek.SUNDAY
            1 -> DayOfWeek.MONDAY
            2 -> DayOfWeek.TUESDAY
            3 -> DayOfWeek.WEDNESDAY
            4 -> DayOfWeek.THURSDAY
            5 -> DayOfWeek.FRIDAY
            6 -> DayOfWeek.SATURDAY
            else -> DayOfWeek.MONDAY // Default
        }
        return javaDayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    fun intToDayOfWeek(dayOfWeekInt: Int): DayOfWeek {
        // dayOfWeekInt: 0=Sunday, 1=Monday, ..., 6=Saturday
        return when (dayOfWeekInt) {
            0 -> DayOfWeek.SUNDAY
            1 -> DayOfWeek.MONDAY
            2 -> DayOfWeek.TUESDAY
            3 -> DayOfWeek.WEDNESDAY
            4 -> DayOfWeek.THURSDAY
            5 -> DayOfWeek.FRIDAY
            6 -> DayOfWeek.SATURDAY
            else -> DayOfWeek.MONDAY // Default or throw error
        }
    }

    fun dayOfWeekToInt(dayOfWeek: DayOfWeek): Int {
        // DayOfWeek: MONDAY(1) ... SUNDAY(7)
        // Convert to 0=Sunday, 1=Monday, ..., 6=Saturday convention
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
        }
    }

    fun daysBetween(startDate: LocalDate, endDate: LocalDate): Long {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate)
    }

    fun getStartOfWeek(date: LocalDate): LocalDate {
        // This function determines the start of the week relative to the given date.
        // If locale uses Monday as start, it returns Monday. If Sunday, it returns Sunday.
        // For simplicity, we assume default locale setting or can specify.
        // DayOfWeek.value is 1 for MONDAY, ..., 7 for SUNDAY.
        // If we want week to start on Sunday (value 7 in DayOfWeek enum), and our convention is 0 for Sunday:
        val currentDayOfWeekValue = date.dayOfWeek.value // 1=Mon, ..., 7=Sun
        val daysToSubtract = if (currentDayOfWeekValue == DayOfWeek.SUNDAY.value) 0 else currentDayOfWeekValue // If Sunday, subtract 0. If Monday (1), subtract 1 to get previous Sunday.
        return date.minusDays(daysToSubtract.toLong())
    }

    fun millisToLocalDate(millis: Long): LocalDate {
        return LocalDateTime.ofEpochSecond(millis / 1000, 0, ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
            .toLocalDate()
    }
}