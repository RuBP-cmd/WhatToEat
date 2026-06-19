package me.normal.whattoeat.ui.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import me.normal.whattoeat.ui.components.AppTopBar
@Composable
fun CalendarScreen(onBack: () -> Unit) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    var daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val tempCalendar = calendar.clone() as Calendar
    tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
    var firstDayOfWeek = (tempCalendar.get(Calendar.DAY_OF_WEEK) - 1)

    CalendarContent(
        calendar = calendar,
        daysInMonth = daysInMonth,
        firstDayOfWeek = firstDayOfWeek,
        onBack = onBack,
        onPrevMonth = {
            val next = calendar.clone() as Calendar
            next.add(Calendar.MONTH, -1)
            calendar = next
        },
        onNextMonth = {
            val next = calendar.clone() as Calendar
            next.add(Calendar.MONTH, 1)
            calendar = next
        }
    )
}

@Composable
private fun CalendarContent(
    calendar: Calendar,
    daysInMonth: Int,
    firstDayOfWeek: Int,
    onBack: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Scaffold(
        topBar = { AppTopBar(onBack, "Calendar", {}) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onPrevMonth) { Text("上月") }
                Text("${calendar.get(Calendar.YEAR)}年 ${calendar.get(Calendar.MONTH) + 1}月")
                Button(onClick = onNextMonth) { Text("下月") }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("日", "一", "二", "三", "四", "五", "六").forEach {
                    Text(it, modifier = Modifier.weight(1f))
                }
            }

            LazyVerticalGrid(columns = GridCells.Fixed(7)) {
                items(firstDayOfWeek) { Spacer(Modifier) }
                items(daysInMonth) { index ->
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}