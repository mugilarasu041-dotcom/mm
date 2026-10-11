package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.MonthUtils
import com.example.model.TrackerData
import com.example.model.calculateMonthlyAnalytics
import com.example.viewmodel.SaveState

@Composable
fun TrackerPrintableLayout(
    tracker: TrackerData,
    saveState: SaveState,
    onProfileNameChange: (String) -> Unit,
    onYearMonthSelected: (year: Int, month: Int) -> Unit,
    onHabitNameChange: (habitId: Int, newName: String) -> Unit,
    onDayToggle: (habitId: Int, day: Int) -> Unit,
    onSleepSelect: (day: Int, hours: Int?) -> Unit,
    onGoalsChange: (String) -> Unit,
    onAchievementsChange: (String) -> Unit,
    onImprovementsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDaysInMonth = MonthUtils.getDaysInMonth(tracker.year, tracker.month)
    val analytics = calculateMonthlyAnalytics(tracker)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .testTag("tracker_printable_layout")
    ) {
        // HEADER
        HeaderSection(
            profileName = tracker.profileName,
            selectedYear = tracker.year,
            selectedMonth = tracker.month,
            saveState = saveState,
            overallDisciplineScore = analytics.overallDisciplineScore,
            onProfileNameChange = onProfileNameChange,
            onYearMonthSelected = onYearMonthSelected
        )

        Spacer(modifier = Modifier.height(10.dp))

        // SECTION 1: HABITS / DAYS
        HabitTrackerGrid(
            habits = tracker.habits,
            totalDaysInMonth = totalDaysInMonth,
            onHabitNameChange = onHabitNameChange,
            onDayToggle = onDayToggle
        )

        Spacer(modifier = Modifier.height(10.dp))

        // SECTION 2: SLEEP
        SleepTrackerGrid(
            sleepEntries = tracker.sleepEntries,
            totalDaysInMonth = totalDaysInMonth,
            sleepMetrics = analytics.sleepMetrics,
            onSleepSelect = onSleepSelect
        )

        Spacer(modifier = Modifier.height(10.dp))

        // SECTION 3: MONTHLY REFLECTION
        MonthlyReflectionSection(
            goals = tracker.goals,
            achievements = tracker.achievements,
            improvements = tracker.improvements,
            onGoalsChange = onGoalsChange,
            onAchievementsChange = onAchievementsChange,
            onImprovementsChange = onImprovementsChange
        )
    }
}
