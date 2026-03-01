package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.warning
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.enums.EventStatus
import java.time.LocalDate


@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    status: EventStatus?,
//    hasEvents: Boolean,
    onClick: () -> Unit
) {
    LogComposable("DayCell") {
        val scheme = MaterialTheme.colorScheme

        val bgColor = when {
            isSelected -> scheme.primary
            isToday -> scheme.secondary
            else -> scheme.surfaceVariant
        }

        val textColor = when {
            isSelected -> scheme.onPrimary
            isToday -> scheme.onSecondary
            else -> scheme.onSurface
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor
            )

            if (status != null) {

                val dotColor = when {
                    isSelected -> scheme.onPrimary
                    else -> when (status) {
                        EventStatus.ACCEPTED -> scheme.primary
                        EventStatus.PENDING -> scheme.warning
                        EventStatus.DECLINED -> scheme.error
                        EventStatus.CANCELLED -> scheme.outline
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }


//        if (hasEvents) {
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 6.dp)
//                    .size(6.dp)
//                    .clip(CircleShape)
//                    .background(
//                        when {
//                            isSelected -> scheme.onPrimary
//                            isToday -> scheme.onSecondary
//                            else -> scheme.primary
//                        }
//                    )
//            )
//        }
        }
    }
}
