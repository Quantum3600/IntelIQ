package com.techbrains.inteliq

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.brahmkshatriya.betterwindow.BetterWindow
import dev.brahmkshatriya.betterwindow.platform.overrideTitleBarAppearance

fun main() = application {
    val windowState = rememberWindowState(
        width = 1280.dp,
        height = 800.dp,
        position = WindowPosition(Alignment.Center)
    )

    BetterWindow(
        onCloseRequest = ::exitApplication,
        title = "IntelIQ",
        windowState = windowState,
        allowDrawingInTitleBar = true,
    ) {
        App(
            onThemeChanged = { isDark ->
                overrideTitleBarAppearance(isDark)
            },
        )
    }
}