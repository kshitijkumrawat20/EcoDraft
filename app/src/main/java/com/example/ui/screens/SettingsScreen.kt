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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.EchoViewModel
import com.example.ui.theme.EchoBackground
import com.example.ui.theme.EchoOnPrimary
import com.example.ui.theme.EchoOnSurface
import com.example.ui.theme.EchoOnSurfaceVariant
import com.example.ui.theme.EchoOutline
import com.example.ui.theme.EchoOutlineVariant
import com.example.ui.theme.EchoPrimary
import com.example.ui.theme.EchoPrimaryContainer
import com.example.ui.theme.EchoSageFacts
import com.example.ui.theme.EchoSecondary
import com.example.ui.theme.EchoSurfaceContainer
import com.example.ui.theme.EchoSurfaceContainerLow
import com.example.ui.theme.Newsreader
import com.example.ui.theme.PublicSans
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("settings_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("back_from_settings")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = EchoSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Settings & Identity",
                    fontFamily = Newsreader,
                    fontSize = 20.sp,
                    color = EchoOnSurface
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.2f), thickness = 0.8.dp)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Local Storage Card
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EchoSurfaceContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "User",
                                    tint = EchoPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Bedside Guest (Local Storage)",
                                    fontFamily = PublicSans,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EchoOnSurface
                                )
                                Text(
                                    text = "Stored safely on device with Room Database",
                                    fontFamily = PublicSans,
                                    fontSize = 12.sp,
                                    color = EchoSecondary
                                )
                            }
                        }

                        HorizontalDivider(color = EchoOutlineVariant.copy(alpha = 0.15f), thickness = 0.8.dp)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Privacy",
                                tint = EchoSageFacts,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "100% Private & Offline First. All entries saved locally.",
                                fontFamily = PublicSans,
                                fontSize = 12.sp,
                                color = EchoSageFacts
                            )
                        }
                    }
                }

                // Gemini Intelligence Configuration Card
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI",
                                tint = EchoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Gemini Intelligence Engine",
                                fontFamily = PublicSans,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = EchoOnSurface
                            )
                        }

                        Text(
                            text = "• Untangling & Deconstruction: gemini-3.5-flash\n• Complex Weekly Synthesis: gemini-3.1-pro-preview\n• Quick Metadata: gemini-3.1-flash-lite-preview",
                            fontFamily = PublicSans,
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            color = EchoSecondary
                        )

                        Text(
                            text = "API Key: Configured securely via Secrets panel (BuildConfig)",
                            fontFamily = PublicSans,
                            fontSize = 11.sp,
                            color = EchoSageFacts
                        )
                    }
                }

                // Test Bedside Prompts Section
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Sample Midnight Audio Prompts",
                            fontFamily = PublicSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = EchoOnSurface
                        )
                        Text(
                            text = "Tap a sample thought below to simulate live bedtime voice transcription & untangling:",
                            fontFamily = Newsreader,
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                            color = EchoSecondary
                        )

                        val samplePrompts = listOf(
                            "“I’m thinking about tomorrow’s presentation and why my stomach is in knots…”",
                            "“Felt overwhelmed after the lab meeting. Need to send an email to Dr. Chen before Friday morning.”",
                            "“Can't sleep because of rent split math. The spreadsheet numbers are accurate, just need approval.”"
                        )

                        samplePrompts.forEach { prompt ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EchoSurfaceContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.requestRecording(sampleText = prompt)
                                    }
                            ) {
                                Text(
                                    text = prompt,
                                    fontFamily = Newsreader,
                                    fontSize = 13.sp,
                                    color = EchoOnSurfaceVariant,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // Export Entries (Premium Feature)
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Export",
                                tint = EchoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Export All Entries",
                                fontFamily = PublicSans,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = EchoOnSurface
                            )
                        }

                        Text(
                            text = "Export your journal entries as formatted plain text, ready to share or save.",
                            fontFamily = Newsreader,
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                            color = EchoSecondary
                        )

                        Button(
                            onClick = { viewModel.exportEntries(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EchoSurfaceContainer,
                                contentColor = EchoOnSurface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_entries_button")
                        ) {
                            Text(
                                text = "Export as Plain Text",
                                fontFamily = PublicSans,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // App version & philosophy
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Echo Drafts v1.0",
                        fontFamily = PublicSans,
                        fontSize = 11.sp,
                        color = EchoOutline
                    )
                    Text(
                        text = "An intimate sanctuary for quiet, late-night decompression.",
                        fontFamily = Newsreader,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = EchoSecondary
                    )
                }
            }
        }
    }
}
