package com.techbrains.inteliq.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import inteliq.app.shared.generated.resources.Res
import io.github.alexzhirkevich.compottie.Lottie
import org.jetbrains.compose.resources.ExperimentalResourceApi

private fun Color.isDark(): Boolean {
    val luminance = 0.2126f * red + 0.7152f * green + 0.0722f * blue
    return luminance < 0.5f
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LottieLoader(
    modifier: Modifier = Modifier,
    size: Dp = 700.dp,
    isDark: Boolean = MaterialTheme.colorScheme.background.isDark()
) {
    val path = if (isDark) "files/q_load_dark.json" else "files/q_load_light.json"

    val composition by rememberLottieComposition(path) {
        val animationBytes = Res.readBytes(path)
        LottieCompositionSpec.JsonString(animationBytes.decodeToString())
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        composition?.let { comp ->
            Lottie(
                painter = rememberLottiePainter(
                    composition = comp,
                    iterations = Compottie.IterateForever
                ),
                contentDescription = "Anim",
                modifier = Modifier.size(size)
            )
        }
    }
}

@Preview
@Composable
fun LottieLoaderLightPreview() {
    Box(
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieLoader(
            size = 100.dp,
            isDark = false
        )
    }
}

@Preview
@Composable
fun LottieLoaderDarkPreview() {
    Box(
        modifier = Modifier
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieLoader(
            size = 100.dp,
            isDark = true
        )
    }
}
