package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthlyReflectionSection(
    goals: String,
    achievements: String,
    improvements: String,
    onGoalsChange: (String) -> Unit,
    onAchievementsChange: (String) -> Unit,
    onImprovementsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reflection_section")
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 3
        ) {
            // Box 1: MY MONTHLY GOALS
            ReflectionBox(
                title = "MY MONTHLY GOALS",
                value = goals,
                onValueChange = onGoalsChange,
                placeholder = "• Goal 1...\n• Goal 2...\n• Goal 3...",
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = 260.dp)
                    .padding(start = 0.dp, top = 0.dp, end = 4.dp, bottom = 8.dp)
                    .testTag("box_goals")
            )

            // Box 2: WHAT I HAVE ACHIEVED THIS MONTH
            ReflectionBox(
                title = "WHAT I HAVE ACHIEVED THIS MONTH",
                value = achievements,
                onValueChange = onAchievementsChange,
                placeholder = "• Achievement 1...\n• Key Win 2...",
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = 260.dp)
                    .padding(start = 2.dp, top = 0.dp, end = 2.dp, bottom = 8.dp)
                    .testTag("box_achievements")
            )

            // Box 3: WHAT SHOULD I IMPROVE
            ReflectionBox(
                title = "WHAT SHOULD I IMPROVE",
                value = improvements,
                onValueChange = onImprovementsChange,
                placeholder = "• Focus area 1...\n• Habit tweak 2...",
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = 260.dp)
                    .padding(start = 4.dp, top = 0.dp, end = 0.dp, bottom = 8.dp)
                    .testTag("box_improvements")
            )
        }
    }
}

@Composable
private fun ReflectionBox(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(1.5.dp, MaterialTheme.colorScheme.onSurface)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                .border(0.75.dp, MaterialTheme.colorScheme.onSurface)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Editable Content Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .padding(10.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
