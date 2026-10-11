package com.example.model

import java.util.Calendar
import java.util.GregorianCalendar

data class Habit(
    val id: Int,
    val name: String,
    val completedDays: Set<Int> = emptySet()
)

data class TrackerData(
    val year: Int,
    val month: Int, // 1..12
    val profileName: String = "",
    val creatorTag: String = "Created by Selfdiscipline_",
    val habits: List<Habit> = defaultHabits(),
    val sleepEntries: Map<Int, Int> = emptyMap(), // day -> hours (5..10)
    val goals: String = "",
    val achievements: String = "",
    val improvements: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

fun defaultHabits(): List<Habit> = listOf(
    Habit(1, "Workout"),
    Habit(2, "Study"),
    Habit(3, "Reading"),
    Habit(4, "Water"),
    Habit(5, "Meditation"),
    Habit(6, "Walking"),
    Habit(7, "Coding"),
    Habit(8, "Sleep Early"),
    Habit(9, "No Junk Food"),
    Habit(10, "Custom Habit")
)

object MonthUtils {
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    fun getMonthName(monthNumber: Int): String {
        val index = (monthNumber - 1).coerceIn(0, 11)
        return monthNames[index]
    }

    fun getDaysInMonth(year: Int, monthNumber: Int): Int {
        val calendar = GregorianCalendar(year, monthNumber - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getCurrentYearAndMonth(): Pair<Int, Int> {
        val cal = Calendar.getInstance()
        return Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }
}

// Calculations
data class HabitMetrics(
    val completedDaysCount: Int,
    val totalAvailableDays: Int,
    val percentage: Float
)

data class OverallHabitMetrics(
    val totalCompletions: Int,
    val totalPossibleCompletions: Int,
    val overallPercentage: Float,
    val habitRowMetrics: List<HabitMetrics>,
    val bestHabitName: String,
    val lowestHabitName: String
)

data class SleepMetrics(
    val trackedDaysCount: Int,
    val totalDaysInMonth: Int,
    val averageHours: Float,
    val daysWithSevenPlusHours: Int,
    val daysWithEightPlusHours: Int,
    val goalCompletionPercentage: Float
)

data class MonthlyAnalytics(
    val overallDisciplineScore: Float,
    val habitMetrics: OverallHabitMetrics,
    val sleepMetrics: SleepMetrics,
    val activeHabitsCount: Int,
    val goalsCompletedCount: Int
)

fun calculateOverallHabitMetrics(tracker: TrackerData): OverallHabitMetrics {
    val totalDays = MonthUtils.getDaysInMonth(tracker.year, tracker.month)
    if (tracker.habits.isEmpty() || totalDays == 0) {
        return OverallHabitMetrics(0, 0, 0f, emptyList(), "N/A", "N/A")
    }

    val rowMetrics = mutableListOf<HabitMetrics>()
    var totalCompletions = 0
    var bestCount = -1
    var lowestCount = Int.MAX_VALUE
    var bestHabit = "N/A"
    var lowestHabit = "N/A"

    tracker.habits.forEach { habit ->
        val validCompletions = habit.completedDays.filter { it in 1..totalDays }.size
        totalCompletions += validCompletions
        val pct = if (totalDays > 0) (validCompletions.toFloat() / totalDays) * 100f else 0f
        rowMetrics.add(HabitMetrics(validCompletions, totalDays, pct))

        if (habit.name.isNotBlank()) {
            if (validCompletions > bestCount) {
                bestCount = validCompletions
                bestHabit = habit.name
            }
            if (validCompletions < lowestCount) {
                lowestCount = validCompletions
                lowestHabit = habit.name
            }
        }
    }

    val totalPossible = tracker.habits.size * totalDays
    val overallPct = if (totalPossible > 0) (totalCompletions.toFloat() / totalPossible) * 100f else 0f

    return OverallHabitMetrics(
        totalCompletions = totalCompletions,
        totalPossibleCompletions = totalPossible,
        overallPercentage = overallPct,
        habitRowMetrics = rowMetrics,
        bestHabitName = if (bestCount >= 0) bestHabit else "N/A",
        lowestHabitName = if (lowestCount != Int.MAX_VALUE) lowestHabit else "N/A"
    )
}

fun calculateSleepMetrics(tracker: TrackerData): SleepMetrics {
    val totalDays = MonthUtils.getDaysInMonth(tracker.year, tracker.month)
    val validEntries = tracker.sleepEntries.filter { (day, hrs) -> day in 1..totalDays && hrs in 5..10 }
    
    val trackedCount = validEntries.size
    if (trackedCount == 0) {
        return SleepMetrics(0, totalDays, 0f, 0, 0, 0f)
    }

    val sumHours = validEntries.values.sum()
    val avgHours = sumHours.toFloat() / trackedCount
    val days7Plus = validEntries.values.count { it >= 7 }
    val days8Plus = validEntries.values.count { it >= 8 }
    
    // Sleep goal completion % is percentage of days in month with 7+ hours of sleep
    val goalPct = (days7Plus.toFloat() / totalDays) * 100f

    return SleepMetrics(
        trackedDaysCount = trackedCount,
        totalDaysInMonth = totalDays,
        averageHours = avgHours,
        daysWithSevenPlusHours = days7Plus,
        daysWithEightPlusHours = days8Plus,
        goalCompletionPercentage = goalPct.coerceAtMost(100f)
    )
}

fun calculateMonthlyAnalytics(tracker: TrackerData): MonthlyAnalytics {
    val habitMetrics = calculateOverallHabitMetrics(tracker)
    val sleepMetrics = calculateSleepMetrics(tracker)

    // Overall discipline score weighted between habits (70%) and sleep (30%)
    val disciplineScore = (habitMetrics.overallPercentage * 0.7f) + (sleepMetrics.goalCompletionPercentage * 0.3f)

    // Goals count simple metric from line breaks in goals section
    val goalsCount = tracker.goals.lines().filter { it.trim().isNotBlank() }.size

    return MonthlyAnalytics(
        overallDisciplineScore = disciplineScore,
        habitMetrics = habitMetrics,
        sleepMetrics = sleepMetrics,
        activeHabitsCount = tracker.habits.count { it.name.isNotBlank() },
        goalsCompletedCount = goalsCount
    )
}
