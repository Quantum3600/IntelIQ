package com.techbrains.inteliq

import androidx.compose.ui.window.application
import dev.brahmkshatriya.betterwindow.BetterWindow

fun main() = application {
    BetterWindow(
        onCloseRequest = ::exitApplication,
        title = "IntelIQ",
        allowDrawingInTitleBar = true,
    ) {
        App()
    }
}