package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Habit
import com.example.ui.theme.DisabledCellBg

@Composable
fun HabitTrackerGrid(
    habits: List<Habit>,
    totalDaysInMonth: Int,
    onHabitNameChange: (habitId: Int, newName: String) -> Unit,
    onDayToggle: (habitId: Int, day: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val labelColumnWidth = 150.dp
    val cellWidth = 36.dp
    val summaryColumnWidth = 80.dp
    val rowHeight = 38.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, MaterialTheme.colorScheme.onSurface)
            .background(MaterialTheme.colorScheme.surface)
            .testTag("habit_tracker_grid")
    ) {
        // Table Wrapper
        Row(modifier = Modifier.fillMaxWidth()) {

            // Sticky Left Column (Header & Habit Names)
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
                        text = "HABITS / DAYS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 10 Habit Rows
                habits.forEachIndexed { index, habit ->
                    Box(
                        modifier = Modifier
                            .width(labelColumnWidth)
                            .height(rowHeight)
                            .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}. ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            BasicTextField(
                                value = habit.name,
                                onValueChange = { onHabitNameChange(habit.id, it) },
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("habit_name_${habit.id}")
                            )
                        }
                    }
                }
            }

            // Horizontally Scrollable Grid for Days 1 to 31 + Summary
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState)
            ) {
                Column {
                    // Days Header Row (1..31)
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

                        // Summary Header
                        Box(
                            modifier = Modifier
                                .width(summaryColumnWidth)
                                .height(rowHeight)
                                .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TOTAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Habit Data Rows
                    habits.forEach { habit ->
                        val validCompletedCount = habit.completedDays.count { it <= totalDaysInMonth }
                        val pct = if (totalDaysInMonth > 0) (validCompletedCount.toFloat() / totalDaysInMonth) * 100f else 0f

                        Row {
                            (1..31).forEach { day ->
                                val isDayInMonth = day <= totalDaysInMonth
                                val isChecked = isDayInMonth && habit.completedDays.contains(day)

                                Box(
                                    modifier = Modifier
                                        .width(cellWidth)
                                        .height(rowHeight)
                                        .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                                        .background(
                                            when {
                                                !isDayInMonth -> DisabledCellBg
                                                isChecked -> MaterialTheme.colorScheme.onSurface
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        )
                                        .clickable(enabled = isDayInMonth) {
                                            onDayToggle(habit.id, day)
                                        }
                                        .testTag("habit_${habit.id}_day_$day"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Text(
                                            text = "✓",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.surface
                                        )
                                    }
                                }
                            }

                            // Summary Row Cell
                            Box(
                                modifier = Modifier
                                    .width(summaryColumnWidth)
                                    .height(rowHeight)
                                    .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$validCompletedCount/$totalDaysInMonth\n(${pct.toInt()}%)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
