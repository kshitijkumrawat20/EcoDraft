package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.EchoViewModel
import com.example.ui.Screen
import com.example.ui.screens.EntryDetailScreen
import com.example.ui.screens.PaywallScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.screens.VoiceCaptureScreen
import com.example.ui.screens.WeeklyPatternsScreen
import com.example.ui.theme.EchoBackground
import com.example.ui.theme.EchoDraftsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchoDraftsTheme {
                EchoDraftsApp()
            }
        }
    }
}

@Composable
fun EchoDraftsApp(
    viewModel: EchoViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pending sample text when permission is being requested
    var pendingSampleText by remember { mutableStateOf<String?>(null) }

    // Audio recording permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceCapture(pendingSampleText)
        } else {
            // Graceful fallback: start with simulated text if no mic access
            viewModel.startVoiceCapture(
                sampleText = pendingSampleText
                    ?: "\u201CI\u2019m thinking about tomorrow\u2019s presentation and why my stomach is in knots\u2026\u201D"
            )
        }
        pendingSampleText = null
    }

    // Collect recording requests from the ViewModel and gate through permission check
    LaunchedEffect(Unit) {
        viewModel.recordingRequested.collect { sampleText ->
            // If a sample text is provided, no mic needed — go straight to capture
            if (sampleText != null) {
                viewModel.startVoiceCapture(sampleText)
                return@collect
            }
            // Check mic permission
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                viewModel.startVoiceCapture()
            } else {
                pendingSampleText = sampleText
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Handle system back button — delegate to ViewModel's navigation stack
    BackHandler(enabled = currentScreen !is Screen.Timeline) {
        viewModel.navigateBack()
    }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = EchoBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EchoBackground)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    is Screen.Timeline -> {
                        TimelineScreen(viewModel = viewModel)
                    }
                    is Screen.VoiceCapture -> {
                        VoiceCaptureScreen(viewModel = viewModel)
                    }
                    is Screen.EntryDetail -> {
                        EntryDetailScreen(
                            entryId = screen.entryId,
                            viewModel = viewModel
                        )
                    }
                    is Screen.WeeklyPatterns -> {
                        WeeklyPatternsScreen(viewModel = viewModel)
                    }
                    is Screen.Settings -> {
                        SettingsScreen(viewModel = viewModel)
                    }
                    is Screen.Paywall -> {
                        PaywallScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
