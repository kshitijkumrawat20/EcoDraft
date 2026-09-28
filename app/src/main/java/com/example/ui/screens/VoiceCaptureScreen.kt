package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.EchoViewModel
import com.example.ui.theme.EchoBackground
import com.example.ui.theme.EchoOnPrimary
import com.example.ui.theme.EchoOnSurface
import com.example.ui.theme.EchoOnSurfaceVariant
import com.example.ui.theme.EchoOutline
import com.example.ui.theme.EchoPrimary
import com.example.ui.theme.EchoPrimaryContainer
import com.example.ui.theme.EchoSurfaceContainerHigh
import com.example.ui.theme.Newsreader
import com.example.ui.theme.PublicSans
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

@Composable
fun VoiceCaptureScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.cancelVoiceCapture()
    }

    val elapsedSeconds by viewModel.speechManager.elapsedSeconds.collectAsStateWithLifecycle()
    val liveTranscript by viewModel.speechManager.liveTranscript.collectAsStateWithLifecycle()
    val amplitudes by viewModel.speechManager.audioAmplitudes.collectAsStateWithLifecycle()
    val isUntangling by viewModel.isUntangling.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()

    var showLocationMenu by remember { mutableStateOf(false) }
    val isManualEntryMode by viewModel.isManualEntryMode.collectAsStateWithLifecycle()
    var manualText by remember { mutableStateOf("") }

    // Pulsing and breathing animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val breatheAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheAlpha"
    )

    val formattedTime = remember(elapsedSeconds) {
        val mins = elapsedSeconds / 60
        val secs = elapsedSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    val sampleTranscriptDisplay = if (liveTranscript.isNotBlank()) {
        if (liveTranscript.startsWith("“") || liveTranscript.startsWith("\"")) {
            liveTranscript
        } else {
            "“$liveTranscript”"
        }
    } else {
        "“I’m thinking about tomorrow’s presentation and why my stomach is in knots…”"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("voice_capture_screen")
    ) {
        // Atmospheric Glow Backdrop
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(340.dp)
                .scale(pulseScale)
                .blur(90.dp)
                .background(EchoPrimaryContainer.copy(alpha = breatheAlpha * 0.12f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Anchor
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable { viewModel.cancelVoiceCapture() }
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .testTag("cancel_capture_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = EchoOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cancel",
                        fontFamily = PublicSans,
                        fontSize = 13.sp,
                        color = EchoOnSurfaceVariant
                    )
                }

                Text(
                    text = stringResource(R.string.tap_to_finish),
                    fontFamily = PublicSans,
                    fontSize = 11.sp,
                    color = EchoOutline,
                    textAlign = TextAlign.End
                )
            }

            // Center Stage: Voice Meditation & Intimate Recording Canvas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Serene Elapsed Timer
                Text(
                    text = formattedTime,
                    fontFamily = PublicSans,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = EchoOnSurface.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Gentle Conversational Status Line
                Text(
                    text = if (isManualEntryMode) "Type what\u2019s on your mind" else stringResource(R.string.listening_status),
                    fontFamily = Newsreader,
                    fontStyle = FontStyle.Italic,
                    fontSize = 21.sp,
                    color = EchoOnSurfaceVariant.copy(alpha = 0.95f),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                if (isManualEntryMode) {
                    // Manual Text Entry Area
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF181B24),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EchoPrimaryContainer.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(180.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        BasicTextField(
                            value = manualText,
                            onValueChange = { manualText = it },
                            textStyle = TextStyle(
                                fontFamily = Newsreader,
                                fontSize = 17.sp,
                                lineHeight = 26.sp,
                                color = EchoOnSurface
                            ),
                            cursorBrush = SolidColor(EchoPrimary),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .testTag("manual_text_input"),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (manualText.isEmpty()) {
                                        Text(
                                            "\u201CI can\u2019t stop thinking about\u2026\u201D",
                                            fontFamily = Newsreader,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 17.sp,
                                            color = EchoOnSurfaceVariant.copy(alpha = 0.45f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Switch back to voice mode
                    TextButton(
                        onClick = {
                            viewModel.disableManualEntryMode()
                            viewModel.requestRecording()
                        },
                        modifier = Modifier.testTag("speak_instead_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak",
                            tint = EchoOutline,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Speak instead",
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoOutline
                        )
                    }
                } else {

                // Fluid Voice Waveform Visualization (12 whisper-thin amplitude bars)
                Row(
                    modifier = Modifier
                        .height(64.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    amplitudes.take(12).forEachIndexed { index, amp ->
                        val barHeight = (48.dp * amp).coerceIn(4.dp, 48.dp)
                        val barAlpha = (0.4f + (amp * 0.6f)).coerceIn(0.4f, 1.0f)
                        Box(
                            modifier = Modifier
                                .width(2.5.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(2.dp))
                                .background(EchoPrimary.copy(alpha = barAlpha))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Live Transcript Floating Fragment
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = sampleTranscriptDisplay,
                        fontFamily = Newsreader,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        color = EchoOnSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EchoPrimaryContainer)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.transcribing_realtime),
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoOutline,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Location selector & sample thought triggers
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF181B24),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4F4536).copy(alpha = 0.4f)),
                            modifier = Modifier.clickable { showLocationMenu = true }
                        ) {
                            Text(
                                text = "📍 $selectedLocation ▾",
                                fontFamily = PublicSans,
                                fontSize = 11.sp,
                                color = EchoOnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showLocationMenu,
                            onDismissRequest = { showLocationMenu = false },
                            modifier = Modifier.background(Color(0xFF1C1F28))
                        ) {
                            viewModel.locationOptions.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text(loc, color = EchoOnSurface, fontFamily = PublicSans, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.setLocation(loc)
                                        showLocationMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
                }
            }

            // Tactile Lower Zone: Record & Finish Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Action: Breathing Hollow Ring & Amber Core
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    // Ambient Breathing Pulse Outer Ring
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(pulseScale)
                            .border(1.dp, EchoPrimaryContainer.copy(alpha = 0.15f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .border(1.2.dp, EchoPrimaryContainer.copy(alpha = 0.35f), CircleShape)
                    )

                    // Interactive Tactile Stop Trigger
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EchoSurfaceContainerHigh)
                            .border(1.5.dp, EchoPrimaryContainer.copy(alpha = 0.85f), CircleShape)
                            .clickable(enabled = !isUntangling) {
                                if (isManualEntryMode) {
                                    viewModel.submitManualEntry(manualText)
                                } else {
                                    viewModel.finishAndOrganizeThought()
                                }
                            }
                            .testTag("stop_and_organize_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Soft Ember Inner Core with Subtle Rounded-Square Glyph
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(EchoPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(EchoOnPrimary)
                            )
                        }
                    }
                }

                // Action Guidance Prompt
                Text(
                    text = stringResource(R.string.tap_to_organize),
                    fontFamily = PublicSans,
                    fontSize = 13.sp,
                    color = EchoOnSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Hairline Secondary Dismiss Action
                TextButton(
                    onClick = { viewModel.cancelVoiceCapture() },
                    modifier = Modifier.testTag("discard_draft_button")
                ) {
                    Text(
                        text = stringResource(R.string.discard_draft),
                        fontFamily = PublicSans,
                        fontSize = 11.sp,
                        color = EchoOutline
                    )
                }

                // Type instead button (manual entry fallback)
                if (!isManualEntryMode) {
                    TextButton(
                        onClick = { viewModel.enableManualEntryMode() },
                        modifier = Modifier.testTag("type_instead_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Type",
                            tint = EchoOutline,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Type instead",
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoOutline
                        )
                    }
                }
            }
        }

        // Overlay when untangling with Gemini
        if (isUntangling) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EchoBackground.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1C1F28),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EchoPrimaryContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = EchoPrimary,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Untangling thoughts into prose…",
                            fontFamily = Newsreader,
                            fontStyle = FontStyle.Italic,
                            fontSize = 18.sp,
                            color = EchoOnSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Distilling Facts, Feelings, and Next Steps",
                            fontFamily = PublicSans,
                            fontSize = 12.sp,
                            color = EchoOnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
