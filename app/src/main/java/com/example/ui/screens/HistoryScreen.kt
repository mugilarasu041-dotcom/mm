package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MonthUtils
import com.example.model.TrackerData
import com.example.model.calculateMonthlyAnalytics

@Composable
fun HistoryScreen(
    allTrackers: List<TrackerData>,
    onSelectTracker: (year: Int, month: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sort trackers descending by year and month
    val sortedTrackers = allTrackers.sortedWith(
        compareByDescending<TrackerData> { it.year }.thenByDescending { it.month }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("history_screen")
    ) {
        Text(
            text = "TRACKER HISTORY",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Access all saved past monthly trackers",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        if (sortedTrackers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MaterialTheme.colorScheme.onBackground)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved trackers found yet.\nStart logging habits in the Tracker screen!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sortedTrackers) { tracker ->
                    val analytics = calculateMonthlyAnalytics(tracker)
                    val mName = MonthUtils.getMonthName(tracker.month)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, MaterialTheme.colorScheme.onBackground)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onSelectTracker(tracker.year, tracker.month) }
                            .padding(14.dp)
                            .testTag("history_item_${tracker.year}_${tracker.month}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "$mName ${tracker.year}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                if (tracker.profileName.isNotBlank()) {
                                    Text(
                                        text = "Name: ${tracker.profileName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.onSurface)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${analytics.overallDisciplineScore.toInt()}%",
                                        color = MaterialTheme.colorScheme.surface,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = "Open")
                            }
                        }

                        if (tracker.goals.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Goals: ${tracker.goals.take(80)}${if (tracker.goals.length > 80) "..." else ""}",
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
