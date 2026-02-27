package com.example.jeffenger.utils.helper

import com.example.jeffenger.ui.calendar.CalendarCell
import java.time.DayOfWeek
import java.time.YearMonth

fun buildMonthCells(yearMonth: YearMonth): List<CalendarCell> {
    val firstOfMonth = yearMonth.atDay(1)
    val lastOfMonth = yearMonth.atEndOfMonth()

    // Montag=1 ... Sonntag=7
    val firstDayOfWeekValue = firstOfMonth.dayOfWeek.value
    val leadingEmptyCount = (firstDayOfWeekValue - DayOfWeek.MONDAY.value + 7) % 7

    val daysInMonth = yearMonth.lengthOfMonth()
    val totalCells = leadingEmptyCount + daysInMonth

    // auf volle Wochen auffüllen
    val trailingEmptyCount = (7 - (totalCells % 7)) % 7
    val finalCount = totalCells + trailingEmptyCount

    val cells = ArrayList<CalendarCell>(finalCount)

    // Leading empties
    repeat(leadingEmptyCount) { index ->
        cells.add(CalendarCell(date = null, key = "empty-leading-$index-${yearMonth}"))
    }

    // Days
    for (day in 1..daysInMonth) {
        val date = yearMonth.atDay(day)
        cells.add(CalendarCell(date = date, key = date.toString()))
    }

    // Trailing empties
    repeat(trailingEmptyCount) { index ->
        cells.add(CalendarCell(date = null, key = "empty-trailing-$index-${yearMonth}"))
    }

    return cells
}