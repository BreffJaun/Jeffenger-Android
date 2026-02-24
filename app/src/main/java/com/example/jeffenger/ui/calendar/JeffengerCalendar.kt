package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.CalendarEvent
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.WeekDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.collections.isNotEmpty

@Composable
fun JeffengerCalendar(
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }

    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    Column {
        MonthHeader(state)

        Spacer(Modifier.height(12.dp))

        WeekdayHeader()

        Spacer(Modifier.height(8.dp))

        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                val date = day.date
                val hasEvents = eventsByDate[date]?.isNotEmpty() == true

                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    hasEvents = hasEvents,
                    onClick = { onDateSelected(date) }
                )
            }
        )
    }
}