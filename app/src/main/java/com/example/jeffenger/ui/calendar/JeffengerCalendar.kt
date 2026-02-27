package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.example.jeffenger.utils.helper.buildMonthCells
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun JeffengerCalendar(
    hasEvent: (LocalDate) -> Boolean,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = remember { LocalDate.now() }

    Column {

        MonthHeader(
            month = currentMonth,
            onPrev = { currentMonth = currentMonth.minusMonths(1) },
            onNext = { currentMonth = currentMonth.plusMonths(1) }
        )

        Spacer(Modifier.height(12.dp))

        WeekdayHeader()

        Spacer(Modifier.height(8.dp))

        val cells = remember(currentMonth) {
            buildMonthCells(currentMonth)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cells, key = { it.key }) { cell ->

                if (cell.date == null) {
                    Spacer(Modifier.size(44.dp))
                } else {
                    DayCell(
                        date = cell.date,
                        isSelected = cell.date == selectedDate,
                        isToday = cell.date == today,
                        hasEvents = hasEvent(cell.date),
                        onClick = { onDateSelected(cell.date) }
                    )
                }
            }
        }
    }
}

