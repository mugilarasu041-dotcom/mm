package com.example.ui.screens

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MonthUtils
import com.example.model.TrackerData
import com.example.model.calculateMonthlyAnalytics
import com.example.ui.components.TrackerPrintableLayout
import com.example.viewmodel.SaveState

@Composable
fun TrackerScreen(
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
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("tracker_screen")
    ) {
        // Quick Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TRACKER",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row {
                OutlinedButton(
                    onClick = { printTrackerDocument(context, tracker) },
                    modifier = Modifier.testTag("print_button"),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Print / PDF", modifier = Modifier.padding(end = 4.dp))
                    Text("PRINT / PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        shareTrackerSummary(context, tracker)
                    },
                    modifier = Modifier.testTag("share_button"),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.padding(end = 4.dp))
                    Text("SHARE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Printable / Interactive Digital Tracker Layout
        TrackerPrintableLayout(
            tracker = tracker,
            saveState = saveState,
            onProfileNameChange = onProfileNameChange,
            onYearMonthSelected = onYearMonthSelected,
            onHabitNameChange = onHabitNameChange,
            onDayToggle = onDayToggle,
            onSleepSelect = onSleepSelect,
            onGoalsChange = onGoalsChange,
            onAchievementsChange = onAchievementsChange,
            onImprovementsChange = onImprovementsChange
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

private fun shareTrackerSummary(context: Context, tracker: TrackerData) {
    val monthName = MonthUtils.getMonthName(tracker.month)
    val analytics = calculateMonthlyAnalytics(tracker)

    val summaryText = """
        ✦ WINTER ARC TRACKER ✦
        Name: ${tracker.profileName.ifEmpty { "Self-Discipline Warrior" }}
        Period: $monthName ${tracker.year}
        Discipline Score: ${String.format("%.1f", analytics.overallDisciplineScore)}%
        Habits Completed: ${analytics.habitMetrics.totalCompletions}/${analytics.habitMetrics.totalPossibleCompletions}
        Average Sleep: ${String.format("%.1f", analytics.sleepMetrics.averageHours)} hrs
        
        Goals:
        ${tracker.goals.ifEmpty { "In progress..." }}
    """.trimIndent()

    val intent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        putExtra(android.content.Intent.EXTRA_TEXT, summaryText)
        type = "text/plain"
    }
    context.startActivity(android.content.Intent.createChooser(intent, "Share Winter Arc Tracker"))
}

private fun printTrackerDocument(context: Context, tracker: TrackerData) {
    val monthName = MonthUtils.getMonthName(tracker.month)
    val totalDays = MonthUtils.getDaysInMonth(tracker.year, tracker.month)

    val htmlContent = buildString {
        append("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; color: #000; background: #fff; margin: 20px; }
                    .header { text-align: center; border: 2px solid #000; padding: 12px; margin-bottom: 15px; }
                    .title { font-size: 20px; font-weight: bold; letter-spacing: 2px; }
                    .sub-header { display: flex; justify-content: space-between; margin-top: 10px; font-size: 14px; font-weight: bold; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 15px; font-size: 10px; }
                    th, td { border: 1px solid #000; text-align: center; height: 24px; padding: 2px; }
                    th.label-col, td.label-col { text-align: left; padding-left: 6px; width: 140px; font-weight: bold; }
                    .checked { background-color: #000; color: #fff; font-weight: bold; }
                    .reflection-container { display: flex; gap: 10px; margin-top: 15px; }
                    .box { flex: 1; border: 1.5px solid #000; min-height: 120px; padding: 8px; }
                    .box-title { font-weight: bold; text-align: center; background: #eee; border-bottom: 1px solid #000; margin: -8px -8px 8px -8px; padding: 4px; font-size: 11px; }
                    .box-content { font-size: 11px; white-space: pre-wrap; }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="title">✦ WINTER ARC TRACKER ✦</div>
                    <div class="sub-header">
                        <span>Name: ${tracker.profileName.ifEmpty { "____________" }}</span>
                        <span>Month: $monthName ${tracker.year}</span>
                        <span>Created by Selfdiscipline_</span>
                    </div>
                </div>

                <h3>HABITS / DAYS</h3>
                <table>
                    <tr>
                        <th class="label-col">HABITS / DAYS</th>
        """.trimIndent())

        for (d in 1..totalDays) {
            append("<th>$d</th>")
        }
        append("<th>TOTAL</th></tr>")

        tracker.habits.forEachIndexed { idx, h ->
            val validCount = h.completedDays.count { it <= totalDays }
            append("<tr><td class=\"label-col\">${idx + 1}. ${h.name}</td>")
            for (d in 1..totalDays) {
                if (h.completedDays.contains(d)) {
                    append("<td class=\"checked\">✓</td>")
                } else {
                    append("<td></td>")
                }
            }
            append("<td>$validCount/$totalDays</td></tr>")
        }

        append("""
                </table>

                <h3>SLEEP</h3>
                <table>
                    <tr>
                        <th class="label-col">SLEEP</th>
        """.trimIndent())

        for (d in 1..totalDays) {
            append("<th>$d</th>")
        }
        append("</tr>")

        val sleepOptions = listOf(5, 6, 7, 8, 9, 10)
        sleepOptions.forEach { hrs ->
            append("<tr><td class=\"label-col\">$hrs hrs</td>")
            for (d in 1..totalDays) {
                if (tracker.sleepEntries[d] == hrs) {
                    append("<td class=\"checked\">✓</td>")
                } else {
                    append("<td></td>")
                }
            }
            append("</tr>")
        }

        append("""
                </table>

                <div class="reflection-container">
                    <div class="box">
                        <div class="box-title">MY MONTHLY GOALS</div>
                        <div class="box-content">${tracker.goals}</div>
                    </div>
                    <div class="box">
                        <div class="box-title">WHAT I HAVE ACHIEVED THIS MONTH</div>
                        <div class="box-content">${tracker.achievements}</div>
                    </div>
                    <div class="box">
                        <div class="box-title">WHAT SHOULD I IMPROVE</div>
                        <div class="box-content">${tracker.improvements}</div>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent())
    }

    try {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val docName = "Winter_Arc_${monthName}_${tracker.year}"
                val printAdapter = webView.createPrintDocumentAdapter(docName)
                printManager.print(docName, printAdapter, PrintAttributes.Builder().build())
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to launch print adapter: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
