package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.model.MonthUtils
import com.example.model.TrackerData
import com.example.model.calculateMonthlyAnalytics

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthlyProgressScreen(
    tracker: TrackerData,
    modifier: Modifier = Modifier
) {
    val analytics = calculateMonthlyAnalytics(tracker)
    val monthName = MonthUtils.getMonthName(tracker.month)
    val totalDays = MonthUtils.getDaysInMonth(tracker.year, tracker.month)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("monthly_progress_screen")
    ) {
        // Section Title
        Text(
            text = "MONTHLY ANALYTICS",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "$monthName ${tracker.year} Overview",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // Hero Score Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, MaterialTheme.colorScheme.onBackground)
                .testTag("score_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OVERALL DISCIPLINE SCORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${String.format("%.1f", analytics.overallDisciplineScore)}%",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { (analytics.overallDisciplineScore / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface),
                    color = MaterialTheme.colorScheme.onSurface,
                    trackColor = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Key Stat Cards in FlowRow
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2
        ) {
            StatBox(
                title = "HABIT COMPLETION",
                value = "${String.format("%.1f", analytics.habitMetrics.overallPercentage)}%",
                subtext = "${analytics.habitMetrics.totalCompletions} / ${analytics.habitMetrics.totalPossibleCompletions} check-ins",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp, bottom = 8.dp)
            )

            StatBox(
                title = "SLEEP GOAL",
                value = "${String.format("%.1f", analytics.sleepMetrics.goalCompletionPercentage)}%",
                subtext = "Avg ${String.format("%.1f", analytics.sleepMetrics.averageHours)}h / ${analytics.sleepMetrics.daysWithSevenPlusHours}d 7+ hrs",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, bottom = 8.dp)
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2
        ) {
            StatBox(
                title = "BEST HABIT",
                value = analytics.habitMetrics.bestHabitName,
                subtext = "Top consistent daily execution",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp, bottom = 8.dp)
            )

            StatBox(
                title = "LOWEST HABIT",
                value = analytics.habitMetrics.lowestHabitName,
                subtext = "Needs focus for improvement",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Habit-by-Habit Breakdown
        Text(
            text = "HABIT BREAKDOWN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        tracker.habits.forEach { habit ->
            val count = habit.completedDays.count { it <= totalDays }
            val pct = if (totalDays > 0) (count.toFloat() / totalDays) * 100f else 0f

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = habit.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$count/$totalDays days (${pct.toInt()}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (pct / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .border(0.5.dp, MaterialTheme.colorScheme.onSurface),
                    color = MaterialTheme.colorScheme.onSurface,
                    trackColor = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(1.5.dp, MaterialTheme.colorScheme.onSurface)
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtext,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
