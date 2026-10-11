package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MonthUtils
import com.example.model.TrackerData
import com.example.model.calculateMonthlyAnalytics

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YearlyProgressScreen(
    allTrackers: List<TrackerData>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    onMonthTrackerClicked: (year: Int, month: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var yearDropdownExpanded by remember { mutableStateOf(false) }
    val yearsList = (2024..2030).toList()

    // Filter trackers for selected year
    val yearTrackersMap = (1..12).associateWith { month ->
        allTrackers.firstOrNull { it.year == selectedYear && it.month == month }
            ?: TrackerData(year = selectedYear, month = month)
    }

    // Calculations for the year
    val monthlyScores = yearTrackersMap.values.map { calculateMonthlyAnalytics(it) }
    val avgYearlyScore = if (monthlyScores.isNotEmpty()) monthlyScores.map { it.overallDisciplineScore }.average().toFloat() else 0f
    val totalYearCompletions = monthlyScores.sumOf { it.habitMetrics.totalCompletions }
    val activeMonthsCount = monthlyScores.count { it.habitMetrics.totalCompletions > 0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("yearly_progress_screen")
    ) {
        // Header & Year Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "YEARLY PROGRESS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$selectedYear Discipline Overview",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.5.dp, MaterialTheme.colorScheme.onBackground)
                        .clickable { yearDropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("yearly_year_selector")
                ) {
                    Text(
                        text = selectedYear.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Year")
                }

                DropdownMenu(
                    expanded = yearDropdownExpanded,
                    onDismissRequest = { yearDropdownExpanded = false }
                ) {
                    yearsList.forEach { yr ->
                        DropdownMenuItem(
                            text = { Text(yr.toString(), fontWeight = if (yr == selectedYear) FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                yearDropdownExpanded = false
                                onYearSelected(yr)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Yearly Overview Summary Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, MaterialTheme.colorScheme.onBackground)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = "$selectedYear AVERAGE DISCIPLINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${String.format("%.1f", avgYearlyScore)}%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (avgYearlyScore / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .border(0.5.dp, MaterialTheme.colorScheme.onSurface),
                color = MaterialTheme.colorScheme.onSurface,
                trackColor = Color.LightGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Check-ins: $totalYearCompletions",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Active Months: $activeMonthsCount / 12",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 12 Months Cards Grid
        Text(
            text = "MONTHLY BREAKDOWN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2
        ) {
            (1..12).forEach { month ->
                val tracker = yearTrackersMap[month]!!
                val analytics = calculateMonthlyAnalytics(tracker)
                val mName = MonthUtils.getMonthName(month)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                        .border(1.2.dp, MaterialTheme.colorScheme.onBackground)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onMonthTrackerClicked(selectedYear, month) }
                        .padding(12.dp)
                        .testTag("month_card_$month")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${analytics.overallDisciplineScore.toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { (analytics.overallDisciplineScore / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .border(0.5.dp, MaterialTheme.colorScheme.onSurface),
                        color = MaterialTheme.colorScheme.onSurface,
                        trackColor = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${analytics.habitMetrics.totalCompletions} check-ins • ${String.format("%.1f", analytics.sleepMetrics.averageHours)}h sleep",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
