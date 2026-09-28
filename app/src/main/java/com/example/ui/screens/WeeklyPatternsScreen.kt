package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.WeeklyPatternReport
import com.example.ui.EchoViewModel
import com.example.ui.theme.EchoBackground
import com.example.ui.theme.EchoDustyRoseFeelings
import com.example.ui.theme.EchoOnSurface
import com.example.ui.theme.EchoOnSurfaceVariant
import com.example.ui.theme.EchoOutline
import com.example.ui.theme.EchoOutlineVariant
import com.example.ui.theme.EchoPrimary
import com.example.ui.theme.EchoPrimaryContainer
import com.example.ui.theme.EchoSageFacts
import com.example.ui.theme.EchoSecondary
import com.example.ui.theme.EchoSoftBlueNextSteps
import com.example.ui.theme.EchoSurfaceContainer
import com.example.ui.theme.EchoSurfaceContainerHigh
import com.example.ui.theme.EchoSurfaceContainerLow
import com.example.ui.theme.EchoSurfaceContainerLowest
import com.example.ui.theme.Newsreader
import com.example.ui.theme.PublicSans

@Composable
fun WeeklyPatternsScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val report by viewModel.latestWeeklyPattern.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingWeeklyPatterns.collectAsStateWithLifecycle()

    val pattern = report ?: WeeklyPatternReport(
        dateRangeText = "Oct 16 – Oct 22",
        synthesisQuote = "“You recorded 6 drafts this week. Your racing thoughts peaked on Tuesday evening around deadlines, with relief following each action step.”",
        synthesisDescription = "Quietly distilled from your late-night voice reflections and untangled midnight notes.",
        peakDayTime = "Peak: Tue 23:40",
        dailyTensionLevels = listOf(0.3f, 0.95f, 0.5f, 0.4f, 0.65f, 0.2f, 0.4f),
        recurringFeelings = listOf(
            com.example.data.model.PatternItem("Overwhelmed before starting", "4 entries"),
            com.example.data.model.PatternItem("Late-night restlessness", "3 entries"),
            com.example.data.model.PatternItem("Relief after writing it down", "3 entries")
        ),
        recurringFacts = listOf(
            com.example.data.model.PatternItem("Midterm & assignment submissions", "3 times"),
            com.example.data.model.PatternItem("Sleep schedule shifts", "2 times"),
            com.example.data.model.PatternItem("Living expenses & rent", "2 times")
        ),
        effectiveNextSteps = "Small tangible actions that calmed racing loops: reading drafts aloud, scheduling specific 15-minute review windows.",
        editorialNote = "\"Your narrative cadence slows down notably once thoughts are anchored onto paper. The urgency dissolved after Tuesday's 3-minute voice stream.\""
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("weekly_patterns_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Top Navigation Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("back_from_patterns")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = EchoSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PREMIUM INSIGHT",
                        fontFamily = PublicSans,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = EchoOutline
                    )
                    Text(
                        text = stringResource(R.string.weekly_patterns_title),
                        fontFamily = Newsreader,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Normal,
                        color = EchoOnSurface
                    )
                }

                IconButton(
                    onClick = { viewModel.regenerateWeeklySynthesis() },
                    modifier = Modifier.testTag("regenerate_synthesis")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = EchoPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = EchoSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Date Range
            Text(
                text = pattern.dateRangeText,
                fontFamily = PublicSans,
                fontSize = 13.sp,
                color = EchoSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            )

            HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.2f), thickness = 0.8.dp)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Editorial Synthesis / Narrative Overview
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EchoPrimaryContainer)
                        )
                        Text(
                            text = stringResource(R.string.bedside_synthesis),
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoPrimary
                        )
                    }

                    Text(
                        text = pattern.synthesisQuote,
                        fontFamily = Newsreader,
                        fontStyle = FontStyle.Italic,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        color = EchoOnSurface
                    )

                    Text(
                        text = pattern.synthesisDescription,
                        fontFamily = Newsreader,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = EchoSecondary
                    )
                }

                // Visual Rhythm Ribbon (Temporal Tension timeline)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EchoSurfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.temporal_tension),
                                fontFamily = PublicSans,
                                fontSize = 11.sp,
                                color = EchoSecondary
                            )
                            Text(
                                text = pattern.peakDayTime,
                                fontFamily = PublicSans,
                                fontSize = 11.sp,
                                color = EchoPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 7 Day Whisper Tension Bars
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        val tensionValues = pattern.dailyTensionLevels.let {
                            if (it.size >= 7) it else listOf(0.3f, 0.95f, 0.5f, 0.4f, 0.65f, 0.2f, 0.4f)
                        }
                        val peakIndex = tensionValues.indices.maxByOrNull { tensionValues[it] } ?: 1

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            days.forEachIndexed { i, day ->
                                val tension = tensionValues.getOrElse(i) { 0.3f }
                                val isPeak = i == peakIndex
                                val barHeight = (38.dp * tension).coerceIn(8.dp, 40.dp)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                if (isPeak) EchoPrimaryContainer else EchoOutlineVariant.copy(alpha = 0.4f)
                                            )
                                    )
                                    Text(
                                        text = day,
                                        fontFamily = PublicSans,
                                        fontSize = 11.sp,
                                        color = if (isPeak) EchoPrimary else EchoSecondary.copy(alpha = 0.6f),
                                        fontWeight = if (isPeak) FontWeight.Medium else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Bento Grid Section: Recurring Themes & Topics
                // 1. Recurring Feelings (Dusty Rose Accent)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EchoSurfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EchoDustyRoseFeelings)
                                )
                                Text(
                                    text = stringResource(R.string.recurring_feelings),
                                    fontFamily = PublicSans,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EchoOnSurface
                                )
                            }
                            Text(
                                text = "${pattern.recurringFeelings.size} patterns",
                                fontFamily = PublicSans,
                                fontSize = 11.sp,
                                color = EchoSecondary
                            )
                        }

                        HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.15f), thickness = 0.8.dp)

                        pattern.recurringFeelings.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontFamily = Newsreader,
                                    fontSize = 16.sp,
                                    color = EchoOnSurface
                                )
                                Text(
                                    text = item.countOrLabel,
                                    fontFamily = PublicSans,
                                    fontSize = 11.sp,
                                    color = EchoDustyRoseFeelings
                                )
                            }
                        }
                    }
                }

                // 2. Recurring Facts & Topics (Sage Accent)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EchoSurfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EchoSageFacts)
                                )
                                Text(
                                    text = stringResource(R.string.recurring_facts),
                                    fontFamily = PublicSans,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EchoOnSurface
                                )
                            }
                            Text(
                                text = "${pattern.recurringFacts.size} topics",
                                fontFamily = PublicSans,
                                fontSize = 11.sp,
                                color = EchoSecondary
                            )
                        }

                        HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.15f), thickness = 0.8.dp)

                        pattern.recurringFacts.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontFamily = Newsreader,
                                    fontSize = 16.sp,
                                    color = EchoOnSurface
                                )
                                Text(
                                    text = item.countOrLabel,
                                    fontFamily = PublicSans,
                                    fontSize = 11.sp,
                                    color = EchoSageFacts
                                )
                            }
                        }
                    }
                }

                // 3. Most Effective Next Steps (Soft Blue Accent)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EchoSurfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EchoSoftBlueNextSteps)
                                )
                                Text(
                                    text = stringResource(R.string.most_effective_steps),
                                    fontFamily = PublicSans,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EchoOnSurface
                                )
                            }
                            Text(
                                text = "Calmed loops",
                                fontFamily = PublicSans,
                                fontSize = 11.sp,
                                color = EchoSoftBlueNextSteps
                            )
                        }

                        Text(
                            text = pattern.effectiveNextSteps,
                            fontFamily = Newsreader,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            color = EchoOnSurfaceVariant
                        )
                    }
                }

                // Editorial Note on Emotional Decompression
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EchoSurfaceContainerLowest.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = "Decompression Note",
                            tint = EchoOutline,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = pattern.editorialNote,
                            fontFamily = Newsreader,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = EchoSecondary
                        )
                    }
                }

                // Calm Footer Actions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EchoSurfaceContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.showMessage("Report saved to notebook")
                            }
                            .testTag("save_report_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Bookmark",
                                tint = EchoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.save_report_notebook),
                                fontFamily = PublicSans,
                                fontSize = 13.sp,
                                color = EchoOnSurface
                            )
                        }
                    }

                    TextButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("done_button")
                    ) {
                        Text(
                            text = stringResource(R.string.done_action),
                            fontFamily = PublicSans,
                            fontSize = 13.sp,
                            color = EchoSecondary
                        )
                    }
                }
            }
        }
    }
}
