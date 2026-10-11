package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MonthUtils
import com.example.viewmodel.SaveState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeaderSection(
    profileName: String,
    selectedYear: Int,
    selectedMonth: Int,
    saveState: SaveState,
    overallDisciplineScore: Float,
    onProfileNameChange: (String) -> Unit,
    onYearMonthSelected: (year: Int, month: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var monthDropdownExpanded by remember { mutableStateOf(false) }
    var yearDropdownExpanded by remember { mutableStateOf(false) }

    val yearsList = (2024..2030).toList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, MaterialTheme.colorScheme.onSurface)
            .padding(16.dp)
            .testTag("header_section")
    ) {
        // Main Title
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✦ WINTER ARC TRACKER ✦",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("main_title")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields Row: Name, Month, Year & Creator line
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.Center
        ) {
            // Name Field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Name: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    if (profileName.isEmpty()) {
                        Text(
                            text = "Enter Name...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    BasicTextField(
                        value = profileName,
                        onValueChange = onProfileNameChange,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )
                }
            }

            // Month Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Month: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.onSurface)
                            .clickable { monthDropdownExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("month_selector")
                    ) {
                        Text(
                            text = MonthUtils.getMonthName(selectedMonth),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Month",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = monthDropdownExpanded,
                        onDismissRequest = { monthDropdownExpanded = false }
                    ) {
                        MonthUtils.monthNames.forEachIndexed { index, mName ->
                            DropdownMenuItem(
                                text = { Text(mName, fontWeight = if (index + 1 == selectedMonth) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    monthDropdownExpanded = false
                                    onYearMonthSelected(selectedYear, index + 1)
                                }
                            )
                        }
                    }
                }
            }

            // Year Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Year: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.onSurface)
                            .clickable { yearDropdownExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("year_selector")
                    ) {
                        Text(
                            text = selectedYear.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Year",
                            modifier = Modifier.size(18.dp)
                        )
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
                                    onYearMonthSelected(yr, selectedMonth)
                                }
                            )
                        }
                    }
                }
            }

            // Creator Tag
            Text(
                text = "Created by Selfdiscipline_",
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Row (Auto-save status & Overall score badge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Save status indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (saveState) {
                    is SaveState.Saving -> {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Saving",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Saving...", fontSize = 12.sp, color = Color.Gray)
                    }
                    is SaveState.Saved -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Saved",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("✓ Saved", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    else -> {}
                }
            }

            // Score Badge
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Monthly Discipline: ${String.format("%.1f", overallDisciplineScore)}%",
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
