package com.techbrains.inteliq

import androidx.compose.ui.window.application
import dev.brahmkshatriya.betterwindow.BetterWindow
import dev.brahmkshatriya.betterwindow.platform.overrideTitleBarAppearance

fun main() = application {
    BetterWindow(
        onCloseRequest = ::exitApplication,
        title = "IntelIQ",
        allowDrawingInTitleBar = true,
    ) {
        App(
            onThemeChanged = { isDark ->
                overrideTitleBarAppearance(isDark)
            },
        )
    }
}