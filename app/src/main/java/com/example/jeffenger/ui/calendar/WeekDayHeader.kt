package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun WeekdayHeader() {
    val scheme = MaterialTheme.colorScheme
    val days = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")

    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { d ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = d,
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class CalendarCell(
    val date: LocalDate?,
    val key: String // stable key for grid
)

//private fun buildMonthCells(yearMonth: YearMonth): List<CalendarCell> {
//    val firstOfMonth = yearMonth.atDay(1)
//    val daysInMonth = yearMonth.lengthOfMonth()
//
//    // Monday=1 ... Sunday=7
//    val firstDayDow = firstOfMonth.dayOfWeek.value
//    val leadingEmpty = (firstDayDow - DayOfWeek.MONDAY.value).let { if (it < 0) it + 7 else it }
//
//    val totalCellsTarget = 42 // 6 rows * 7 columns (stabil)
//    val cells = mutableListOf<CalendarCell>()
//
//    repeat(leadingEmpty) { idx ->
//        cells.add(CalendarCell(date = null, key = "e_pre_$idx"))
//    }
//
//    for (day in 1..daysInMonth) {
//        val date = yearMonth.atDay(day)
//        cells.add(CalendarCell(date = date, key = date.toString()))
//    }
//
//    // trailing empty to reach 42
//    while (cells.size < totalCellsTarget) {
//        cells.add(CalendarCell(date = null, key = "e_post_${cells.size}"))
//    }
//
//    return cells
//}