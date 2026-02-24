//package com.example.jeffenger.ui.calendar
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.unit.dp
//import com.example.jeffenger.data.remote.model.CalendarEvent
//import java.time.LocalDate
//import java.time.YearMonth
//import kotlin.collections.isNotEmpty
//
//@Composable
//private fun MonthCalendarCard(
//    yearMonth: YearMonth,
//    selectedDay: LocalDate,
//    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
//    onPrevMonth: () -> Unit,
//    onNextMonth: () -> Unit,
//    onSelectDay: (LocalDate) -> Unit,
//) {
//    val scheme = MaterialTheme.colorScheme
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(24.dp))
//            .background(scheme.surface)
//            .padding(16.dp)
//    ) {
//        MonthHeader(
//            yearMonth = yearMonth,
//            onPrev = onPrevMonth,
//            onNext = onNextMonth
//        )
//
//        Spacer(Modifier.height(12.dp))
//
//        WeekdayHeader()
//
//        Spacer(Modifier.height(10.dp))
//
//        val cells = remember(yearMonth) { buildMonthCells(yearMonth) }
//
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(7),
//            userScrollEnabled = false,
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            items(cells, key = { it.key }) { cell ->
//                DayCell(
//                    cell = cell,
//                    isSelected = cell.date == selectedDay,
//                    hasEvents = cell.date != null && eventsByDate[cell.date]?.isNotEmpty() == true,
//                    onClick = {
//                        cell.date?.let(onSelectDay)
//                    }
//                )
//            }
//        }
//    }
//}