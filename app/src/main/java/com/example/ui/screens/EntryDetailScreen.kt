package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DraftEntry
import com.example.ui.EchoViewModel
import com.example.ui.theme.EchoBackground
import com.example.ui.theme.EchoDustyRoseFeelings
import com.example.ui.theme.EchoOnSurface
import com.example.ui.theme.EchoOnSurfaceVariant
import com.example.ui.theme.EchoOutline
import com.example.ui.theme.EchoOutlineVariant
import com.example.ui.theme.EchoPrimary
import com.example.ui.theme.EchoSageFacts
import com.example.ui.theme.EchoSecondary
import com.example.ui.theme.EchoSoftBlueNextSteps
import com.example.ui.theme.EchoSurfaceContainerLow
import com.example.ui.theme.EchoSurfaceContainerLowest
import com.example.ui.theme.Newsreader
import com.example.ui.theme.PublicSans

@Composable
fun EntryDetailScreen(
    entryId: Long,
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val drafts by viewModel.draftsList.collectAsStateWithLifecycle()
    val entry = drafts.find { it.id == entryId } ?: drafts.firstOrNull()

    var isTranscriptExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    if (entry == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(EchoBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Entry not found", color = EchoOnSurface)
        }
        return
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("entry_detail_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Top Bar Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = EchoSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = entry.displayDate,
                    fontFamily = PublicSans,
                    fontSize = 11.sp,
                    color = EchoSecondary,
                    letterSpacing = 0.5.sp
                )

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("detail_more_options")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More",
                            tint = EchoSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(EchoSurfaceContainerLow)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete entry", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                viewModel.deleteDraft(entry.id)
                            }
                        )
                    }
                }
            }

            HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.15f), thickness = 0.8.dp)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(top = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Hero Thought / Entry Title
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = entry.title,
                        fontFamily = Newsreader,
                        fontSize = 28.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Normal,
                        color = EchoOnSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic",
                            tint = EchoPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Recorded ${entry.durationSeconds}s",
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoSecondary
                        )
                        Text(
                            text = "/",
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoOutlineVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Deconstructed stream",
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoSecondary
                        )
                    }
                }

                HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.15f), thickness = 0.8.dp)

                // 1. Facts Section Card
                if (entry.facts.isNotEmpty()) {
                    BreakdownCard(
                        title = stringResource(R.string.category_facts),
                        accentColor = EchoSageFacts,
                        items = entry.facts,
                        isArrowStyle = false
                    )
                }

                // 2. Feelings Section Card
                if (entry.feelings.isNotEmpty()) {
                    BreakdownCard(
                        title = stringResource(R.string.category_feelings),
                        accentColor = EchoDustyRoseFeelings,
                        items = entry.feelings,
                        isArrowStyle = false
                    )
                }

                // 3. Next Steps Section Card
                if (entry.nextSteps.isNotEmpty()) {
                    BreakdownCard(
                        title = stringResource(R.string.category_next_steps),
                        accentColor = EchoSoftBlueNextSteps,
                        items = entry.nextSteps,
                        isArrowStyle = true
                    )
                }

                // De-emphasized Raw Transcript Section (Collapsible Accordion)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EchoSurfaceContainerLowest,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isTranscriptExpanded = !isTranscriptExpanded }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Transcript",
                                    tint = if (isTranscriptExpanded) EchoPrimary else EchoSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stringResource(R.string.raw_voice_transcript, entry.durationSeconds),
                                    fontFamily = PublicSans,
                                    fontSize = 13.sp,
                                    color = if (isTranscriptExpanded) EchoPrimary else EchoSecondary
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Expand",
                                tint = EchoSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .rotate(if (isTranscriptExpanded) 180f else 0f)
                            )
                        }

                        AnimatedVisibility(
                            visible = isTranscriptExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 18.dp)
                            ) {
                                HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.1f), thickness = 0.8.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Whisper waveform mini indicator
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                ) {
                                    val waveHeights = listOf(6.dp, 14.dp, 8.dp, 18.dp, 12.dp, 20.dp, 10.dp, 6.dp, 15.dp, 11.dp, 6.dp)
                                    waveHeights.forEach { h ->
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(h)
                                                .clip(RoundedCornerShape(1.dp))
                                                .background(EchoPrimary.copy(alpha = 0.7f))
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${entry.durationSeconds / 60}:${String.format("%02d", entry.durationSeconds % 60)}",
                                        fontFamily = PublicSans,
                                        fontSize = 11.sp,
                                        color = EchoSecondary
                                    )
                                }

                                Text(
                                    text = entry.rawTranscript,
                                    fontFamily = Newsreader,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp,
                                    color = EchoSecondary.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable { viewModel.deleteDraft(entry.id) }
                        .padding(8.dp)
                        .testTag("delete_entry_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = EchoSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.delete_entry),
                        fontFamily = PublicSans,
                        fontSize = 13.sp,
                        color = EchoSecondary.copy(alpha = 0.7f)
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, entry.title)
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${entry.title}\n\nFacts:\n${entry.facts.joinToString("\n• ")}\n\nFeelings:\n${entry.feelings.joinToString("\n• ")}\n\nNext Steps:\n${entry.nextSteps.joinToString("\n→ ")}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share insight"))
                        }
                        .padding(8.dp)
                        .testTag("share_insight_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = "Share",
                        tint = EchoSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.share_insight),
                        fontFamily = PublicSans,
                        fontSize = 13.sp,
                        color = EchoSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun BreakdownCard(
    title: String,
    accentColor: Color,
    items: List<String>,
    isArrowStyle: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = EchoSurfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.15f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with accent dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Text(
                    text = title.uppercase(),
                    fontFamily = PublicSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = accentColor
                )
            }

            // Items with vertical guide hairline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp)
                    .border(
                        width = 1.dp,
                        color = EchoOutlineVariant.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(0.dp)
                    )
                    .padding(start = 12.dp, top = 2.dp, bottom = 2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    if (isArrowStyle) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "→",
                                fontFamily = Newsreader,
                                fontSize = 16.sp,
                                color = EchoPrimary
                            )
                            Text(
                                text = item,
                                fontFamily = Newsreader,
                                fontSize = 15.sp,
                                lineHeight = 23.sp,
                                color = EchoOnSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = item,
                            fontFamily = Newsreader,
                            fontSize = 15.sp,
                            lineHeight = 23.sp,
                            color = EchoOnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
