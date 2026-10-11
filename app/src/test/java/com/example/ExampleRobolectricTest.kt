package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Habit
import com.example.model.MonthUtils
import com.example.model.TrackerData
import com.example.model.calculateMonthlyAnalytics
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WINTER ARC", appName)
  }

  @Test
  fun `verify month days calculation and analytics`() {
    val daysOct = MonthUtils.getDaysInMonth(2026, 10)
    assertEquals(31, daysOct)

    val daysFebLeap = MonthUtils.getDaysInMonth(2028, 2)
    assertEquals(29, daysFebLeap)

    val tracker = TrackerData(
      year = 2026,
      month = 10,
      habits = listOf(
        Habit(1, "Workout", completedDays = setOf(1, 2, 3, 4, 5))
      ),
      sleepEntries = mapOf(1 to 8, 2 to 7, 3 to 9)
    )

    val analytics = calculateMonthlyAnalytics(tracker)
    assertEquals(5, analytics.habitMetrics.totalCompletions)
    assertEquals(8.0f, analytics.sleepMetrics.averageHours, 0.1f)
  }
}
