package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import com.example.ui.NavigationTab
import com.example.ui.Screen
import com.example.ui.theme.EchoBackground
import com.example.ui.theme.EchoDustyRoseFeelings
import com.example.ui.theme.EchoOnPrimaryContainer
import com.example.ui.theme.EchoOnSurface
import com.example.ui.theme.EchoOnSurfaceVariant
import com.example.ui.theme.EchoOutline
import com.example.ui.theme.EchoOutlineVariant
import com.example.ui.theme.EchoPrimary
import com.example.ui.theme.EchoPrimaryContainer
import com.example.ui.theme.EchoSageFacts
import com.example.ui.theme.EchoSecondary
import com.example.ui.theme.EchoSecondaryContainer
import com.example.ui.theme.EchoSecondaryFixed
import com.example.ui.theme.EchoSoftBlueNextSteps
import com.example.ui.theme.EchoSurface
import com.example.ui.theme.EchoSurfaceContainer
import com.example.ui.theme.EchoSurfaceContainerHigh
import com.example.ui.theme.EchoSurfaceContainerLow
import com.example.ui.theme.Newsreader
import com.example.ui.theme.PublicSans
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

@Composable
fun TimelineScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    val drafts by viewModel.draftsList.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val weeklyEntryCount by viewModel.weeklyEntryCount.collectAsStateWithLifecycle()
    val canRecordEntry by viewModel.canRecordEntry.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val displayedEntries = if (isSearchActive && searchQuery.isNotBlank()) searchResults else drafts

    // Pulse animation for mic button
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("timeline_screen"),
        containerColor = EchoBackground,
        bottomBar = {
            EchoBottomNavigationBar(
                activeTab = activeTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Top App Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "Notes",
                                tint = EchoOnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Echo Drafts",
                                fontFamily = Newsreader,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Medium,
                                color = EchoOnSurface
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                            IconButton(
                                onClick = { viewModel.toggleSearch() },
                                modifier = Modifier.testTag("search_button")
                            ) {
                                Icon(
                                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                    contentDescription = if (isSearchActive) "Close search" else "Search",
                                    tint = if (isSearchActive) EchoPrimary else EchoOnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.navigateTo(Screen.Settings) },
                                modifier = Modifier.testTag("settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = EchoOnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Intro Header & Gentle Prompt
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 20.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.headline_quote),
                            fontFamily = Newsreader,
                            fontStyle = FontStyle.Italic,
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Normal,
                            color = EchoOnSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.headline_sub),
                            fontFamily = PublicSans,
                            fontSize = 13.sp,
                            color = EchoSecondary
                        )
                    }
                }

                // Search Field (collapsible)
                item {
                    AnimatedVisibility(
                        visible = isSearchActive,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EchoSurfaceContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = EchoSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.updateSearchQuery(it) },
                                    textStyle = TextStyle(
                                        fontFamily = PublicSans,
                                        fontSize = 14.sp,
                                        color = EchoOnSurface
                                    ),
                                    cursorBrush = SolidColor(EchoPrimary),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { innerTextField ->
                                        Box {
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    "Search thoughts\u2026",
                                                    fontFamily = PublicSans,
                                                    fontSize = 14.sp,
                                                    color = EchoSecondary
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Weekly Pattern Report Card
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EchoSurfaceContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .clickable { viewModel.navigateTo(Screen.WeeklyPatterns) }
                            .testTag("weekly_patterns_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(EchoSurfaceContainerHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Insights,
                                        contentDescription = "Insights",
                                        tint = EchoPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.weekly_patterns_title),
                                            fontFamily = PublicSans,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = EchoOnSurface
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Lock",
                                            tint = EchoSecondary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stringResource(R.string.recurring_themes_count, 4),
                                        fontFamily = PublicSans,
                                        fontSize = 13.sp,
                                        color = EchoSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open Patterns",
                                tint = EchoSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Notebook Entries List
                items(displayedEntries, key = { it.id }) { entry ->
                    DraftEntryItem(
                        entry = entry,
                        onClick = { viewModel.navigateTo(Screen.EntryDetail(entry.id)) },
                        onDelete = { viewModel.deleteDraft(entry.id) }
                    )
                }

                // Empty state if no entries
                if (displayedEntries.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nothing here yet.",
                                fontFamily = Newsreader,
                                fontStyle = FontStyle.Italic,
                                fontSize = 22.sp,
                                color = EchoOnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Say what's on your mind.",
                                fontFamily = PublicSans,
                                fontSize = 14.sp,
                                color = EchoSecondary
                            )
                        }
                    }
                }
            }

            // Floating Record Zone & Usage Counter
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Usage counter badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EchoSurfaceContainer.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.2f)),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.voice_entries_used, weeklyEntryCount, 3),
                        fontFamily = PublicSans,
                        fontSize = 11.sp,
                        color = EchoSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // Warm pulse mic button
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .scale(pulseScale)
                            .border(1.dp, EchoPrimaryContainer.copy(alpha = 0.25f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(EchoPrimaryContainer)
                            .border(1.dp, EchoPrimary.copy(alpha = 0.4f), CircleShape)
                            .clickable { viewModel.requestRecording() }
                            .testTag("floating_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Record thought",
                            tint = EchoOnPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DraftEntryItem(
    entry: DraftEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val formattedDuration = remember(entry.durationSeconds) {
        val mins = entry.durationSeconds / 60
        val secs = entry.durationSeconds % 60
        String.format("%d:%02d", mins, secs)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(top = 16.dp, bottom = 20.dp)
            .testTag("draft_entry_${entry.id}")
    ) {
        // Top row: timestamp + category semantic dot labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entry.displayDate,
                fontFamily = PublicSans,
                fontSize = 11.sp,
                color = EchoSecondary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (entry.hasFacts) {
                    CategoryDotTag(name = "Facts", color = EchoSageFacts)
                }
                if (entry.hasFeelings) {
                    CategoryDotTag(name = "Feelings", color = EchoDustyRoseFeelings)
                }
                if (entry.hasNextSteps) {
                    CategoryDotTag(name = "Next Steps", color = EchoSecondaryFixed)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Prose quote
        Text(
            text = entry.quotePreview,
            fontFamily = Newsreader,
            fontSize = 17.sp,
            lineHeight = 27.sp,
            fontWeight = FontWeight.Light,
            color = EchoOnSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom row: audio length, location, overflow menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Audio",
                        tint = EchoSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = formattedDuration,
                        fontFamily = PublicSans,
                        fontSize = 11.sp,
                        color = EchoSecondary
                    )
                }

                Text(
                    text = entry.recordedLocation,
                    fontFamily = PublicSans,
                    fontSize = 11.sp,
                    color = EchoSecondary
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More options",
                        tint = EchoSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(EchoSurfaceContainer)
                ) {
                    DropdownMenuItem(
                        text = { Text("Open details", color = EchoOnSurface, fontFamily = PublicSans, fontSize = 13.sp) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete entry", color = MaterialTheme.colorScheme.error, fontFamily = PublicSans, fontSize = 13.sp) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.2f), thickness = 0.8.dp)
    }
}

@Composable
fun CategoryDotTag(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = name,
            fontFamily = PublicSans,
            fontSize = 11.sp,
            color = color
        )
    }
}

@Composable
fun EchoBottomNavigationBar(
    activeTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = EchoSurfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, EchoOutlineVariant.copy(alpha = 0.2f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drafts Tab
            BottomNavItem(
                icon = Icons.Default.EditNote,
                label = stringResource(R.string.nav_drafts),
                isActive = activeTab == NavigationTab.DRAFTS,
                onClick = { onTabSelected(NavigationTab.DRAFTS) },
                testTag = "nav_drafts"
            )

            // Capture Tab
            BottomNavItem(
                icon = Icons.Default.Mic,
                label = stringResource(R.string.nav_capture),
                isActive = activeTab == NavigationTab.CAPTURE,
                onClick = { onTabSelected(NavigationTab.CAPTURE) },
                testTag = "nav_capture"
            )

            // Reflect Tab
            BottomNavItem(
                icon = Icons.Default.Spa,
                label = stringResource(R.string.nav_reflect),
                isActive = activeTab == NavigationTab.REFLECT,
                onClick = { onTabSelected(NavigationTab.REFLECT) },
                testTag = "nav_reflect"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val tint = if (isActive) EchoPrimary else EchoSecondary

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = PublicSans,
            fontSize = 11.sp,
            color = tint
        )
    }
}
