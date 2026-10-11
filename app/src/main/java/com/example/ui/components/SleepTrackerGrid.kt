package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SleepMetrics
import com.example.ui.theme.DisabledCellBg

@Composable
fun SleepTrackerGrid(
    sleepEntries: Map<Int, Int>, // day -> hours
    totalDaysInMonth: Int,
    sleepMetrics: SleepMetrics,
    onSleepSelect: (day: Int, hours: Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val sleepOptions = listOf(5, 6, 7, 8, 9, 10)
    val labelColumnWidth = 150.dp
    val cellWidth = 36.dp
    val summaryColumnWidth = 80.dp
    val rowHeight = 36.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, MaterialTheme.colorScheme.onSurface)
            .background(MaterialTheme.colorScheme.surface)
            .testTag("sleep_tracker_grid")
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Sticky Left Column (SLEEP & Hour Labels)
            Column(
                modifier = Modifier
                    .width(labelColumnWidth)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Header Cell
                Box(
                    modifier = Modifier
                        .width(labelColumnWidth)
                        .height(rowHeight)
                        .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "SLEEP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 6 Sleep Rows
                sleepOptions.forEach { hrs ->
                    Box(
                        modifier = Modifier
                            .width(labelColumnWidth)
                            .height(rowHeight)
                            .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "$hrs hrs",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Horizontally Scrollable Grid for Days 1 to 31
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState)
            ) {
                Column {
                    // Header Row (1..31)
                    Row {
                        (1..31).forEach { day ->
                            val isDayInMonth = day <= totalDaysInMonth
                            Box(
                                modifier = Modifier
                                    .width(cellWidth)
                                    .height(rowHeight)
                                    .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                                    .background(
                                        if (isDayInMonth) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                        else DisabledCellBg
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = if (isDayInMonth) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDayInMonth) MaterialTheme.colorScheme.onSurface else Color.LightGray
                                )
                            }
                        }

                        // Summary Column Header
                        Box(
                            modifier = Modifier
                                .width(summaryColumnWidth)
                                .height(rowHeight)
                                .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "STATS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Sleep Hours Rows
                    sleepOptions.forEach { hrs ->
                        Row {
                            (1..31).forEach { day ->
                                val isDayInMonth = day <= totalDaysInMonth
                                val isSelected = isDayInMonth && sleepEntries[day] == hrs

                                Box(
                                    modifier = Modifier
                                        .width(cellWidth)
                                        .height(rowHeight)
                                        .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                                        .background(
                                            when {
                                                !isDayInMonth -> DisabledCellBg
                                                isSelected -> MaterialTheme.colorScheme.onSurface
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        )
                                        .clickable(enabled = isDayInMonth) {
                                            if (isSelected) {
                                                onSleepSelect(day, null)
                                            } else {
                                                onSleepSelect(day, hrs)
                                            }
                                        }
                                        .testTag("sleep_${hrs}hrs_day_$day"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Text(
                                            text = "✓",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.surface
                                        )
                                    }
                                }
                            }

                            // Summary Stats Column across rows
                            if (hrs == 5) {
                                Box(
                                    modifier = Modifier
                                        .width(summaryColumnWidth)
                                        .height(rowHeight * 6)
                                        .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Avg:\n${String.format("%.1f", sleepMetrics.averageHours)}h",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "7+ Hrs:\n${sleepMetrics.daysWithSevenPlusHours}d",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
