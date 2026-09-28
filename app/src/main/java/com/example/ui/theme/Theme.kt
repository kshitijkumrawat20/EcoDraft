package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EchoDarkColorScheme = darkColorScheme(
    primary = EchoPrimary,
    onPrimary = EchoOnPrimary,
    primaryContainer = EchoPrimaryContainer,
    onPrimaryContainer = EchoOnPrimaryContainer,
    secondary = EchoSecondary,
    onSecondary = EchoOnSecondary,
    secondaryContainer = EchoSecondaryContainer,
    onSecondaryContainer = EchoOnSecondaryContainer,
    tertiary = EchoSageFacts,
    background = EchoBackground,
    onBackground = EchoOnSurface,
    surface = EchoSurface,
    onSurface = EchoOnSurface,
    surfaceVariant = EchoSurfaceContainerHighest,
    onSurfaceVariant = EchoOnSurfaceVariant,
    outline = EchoOutline,
    outlineVariant = EchoOutlineVariant,
    error = EchoError,
    onError = EchoOnError,
    errorContainer = EchoErrorContainer,
    onErrorContainer = EchoOnErrorContainer
)

@Composable
fun EchoDraftsTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = EchoBackground.toArgb()
                window.navigationBarColor = EchoSurfaceContainerLow.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = EchoDarkColorScheme,
        typography = Typography,
        content = content
    )
}
