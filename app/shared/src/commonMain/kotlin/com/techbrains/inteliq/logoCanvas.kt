package com.techbrains.inteliq

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val Dark = Color(0xFF2D2D2D)
private val Green = Color(0xFF00E522)
private val Yellow = Color(0xFFEBFF14)
private val Red = Color(0xFFFF0000)
private val Blue = Color(0xFF4EB5FF)

// High-resolution SVG canvas coordinate space: 3488 x 776 (4x base resolution)
private const val LOGO_W = 3488f
private const val LOGO_H = 776f

// High-resolution IQ logo canvas coordinate space: 1100 x 776
private const val IQ_W = 1100f
private const val IQ_H = 776f

// Unified "n" shape: arch + legs combined into a single contiguous path to eliminate anti-aliasing seams/white lines
private val nPath = Path().apply {
    moveTo(320f, 776f)
    lineTo(320f, 504f)
    cubicTo(320f, 429.74f, 349.5f, 358.5204f, 402.012f, 306.01f)
    cubicTo(454.52f, 253.5f, 525.74f, 224f, 600f, 224f)
    cubicTo(674.26f, 224f, 745.48f, 253.5f, 797.988f, 306.01f)
    cubicTo(850.5f, 358.5204f, 880f, 429.74f, 880f, 504f)
    lineTo(880f, 776f)
    lineTo(624f, 776f)
    lineTo(624f, 504f)
    cubicTo(624f, 497.628f, 621.508f, 491.516f, 616.988f, 487.012f)
    cubicTo(612.468f, 482.508f, 606.372f, 479.976f, 600f, 479.976f)
    cubicTo(593.628f, 479.976f, 587.516f, 482.508f, 583.012f, 487.012f)
    cubicTo(578.492f, 491.516f, 576f, 497.628f, 576f, 504f)
    lineTo(576f, 776f)
    close()
}

// "L" with rounded inner curve
private val lPath = Path().apply {
    moveTo(1184f, 500f)
    cubicTo(1184f, 511.64f, 1188.752f, 522.804f, 1197.816f, 531.036f)
    cubicTo(1206.88f, 539.264f, 1219.18f, 544f, 1232f, 544f)
    lineTo(1488f, 544f)
    lineTo(1488f, 776f)
    lineTo(1232f, 776f)
    cubicTo(1151.376f, 776f, 1074.052f, 746.92f, 1017.04f, 695.16f)
    cubicTo(961.068f, 644.344f, 929.18f, 575.752f, 928.036f, 504f)
    lineTo(928f, 504f)
    lineTo(928f, 40f)
    lineTo(1184f, 40f)
    lineTo(1184f, 500f)
    close()
}

// "e"
private val ePath = Path().apply {
    moveTo(1684.012f, 249.0632f)
    cubicTo(1741.14f, 218.5268f, 1807.092f, 208.742f, 1870.624f, 221.3796f)
    cubicTo(1934.16f, 234.0172f, 1991.348f, 268.2964f, 2032.444f, 318.3716f)
    cubicTo(2073.54f, 368.4464f, 2096f, 431.22f, 2096f, 496f)
    lineTo(1726.12f, 496f)
    cubicTo(1726.12f, 504.776f, 1727.404f, 513.568f, 1729.988f, 522.092f)
    cubicTo(1736.024f, 541.988f, 1748.772f, 559.18f, 1766.06f, 570.736f)
    cubicTo(1783.352f, 582.288f, 1804.116f, 587.488f, 1824.812f, 585.448f)
    cubicTo(1845.504f, 583.412f, 1864.852f, 574.264f, 1879.556f, 559.56f)
    lineTo(2013.992f, 693.988f)
    cubicTo(1968.184f, 739.796f, 1907.912f, 768.304f, 1843.444f, 774.652f)
    cubicTo(1778.976f, 781.004f, 1714.304f, 764.804f, 1660.44f, 728.812f)
    cubicTo(1606.58f, 692.824f, 1566.864f, 639.268f, 1548.056f, 577.276f)
    cubicTo(1529.252f, 515.288f, 1532.524f, 448.696f, 1557.316f, 388.8484f)
    cubicTo(1582.104f, 329.0004f, 1626.88f, 279.5996f, 1684.012f, 249.0632f)
    close()
}

// "Q" body: three-quarter ring with a notch
private val qPath = Path().apply {
    moveTo(3488f, 408f)
    cubicTo(3488f, 335.2164f, 3466.416f, 264.0676f, 3425.98f, 203.55f)
    cubicTo(3385.544f, 143.0328f, 3328.072f, 95.8652f, 3260.828f, 68.0124f)
    cubicTo(3193.584f, 40.1592f, 3119.592f, 32.87164f, 3048.208f, 47.0712f)
    cubicTo(2976.82f, 61.2704f, 2911.252f, 96.3188f, 2859.784f, 147.7848f)
    cubicTo(2808.32f, 199.2504f, 2773.272f, 264.8216f, 2759.072f, 336.2068f)
    cubicTo(2744.872f, 407.592f, 2752.16f, 481.584f, 2780.012f, 548.828f)
    cubicTo(2807.864f, 616.072f, 2855.032f, 673.544f, 2915.552f, 713.98f)
    cubicTo(2976.068f, 754.416f, 3047.216f, 776f, 3120f, 776f)
    lineTo(3120f, 519.684f)
    cubicTo(3097.912f, 519.684f, 3076.316f, 513.136f, 3057.952f, 500.864f)
    cubicTo(3039.584f, 488.592f, 3025.272f, 471.148f, 3016.816f, 450.74f)
    cubicTo(3008.364f, 430.332f, 3006.152f, 407.876f, 3010.46f, 386.2116f)
    cubicTo(3014.772f, 364.5468f, 3025.408f, 344.6464f, 3041.028f, 329.0272f)
    cubicTo(3056.648f, 313.408f, 3076.548f, 302.7708f, 3098.212f, 298.4616f)
    cubicTo(3119.876f, 294.1524f, 3142.332f, 296.364f, 3162.74f, 304.8172f)
    cubicTo(3183.148f, 313.2704f, 3200.592f, 327.5852f, 3212.864f, 345.9516f)
    cubicTo(3225.136f, 364.318f, 3231.684f, 385.9108f, 3231.684f, 408f)
    lineTo(3488f, 408f)
    close()
}

@Composable
fun InteliqLogo(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    textColor: Color = if (isDarkTheme) Color.White else Dark
) {
    Canvas(modifier = modifier.aspectRatio(LOGO_W / LOGO_H)) {
        val s = size.width / LOGO_W
        // Draw in high-resolution vector coordinates (3488 x 776)
        scale(scaleX = s, scaleY = s, pivot = Offset.Zero) {
            // "i" (left): stem + yellow dot
            drawRect(textColor, topLeft = Offset(16f, 312f), size = Size(256f, 464f))
            drawCircle(Yellow, radius = 146f, center = Offset(146f, 146f))

            // "n": unified arch + legs (no interior seam/white line)
            drawPath(nPath, textColor)

            // "L" + green dot
            drawPath(lPath, textColor)
            drawCircle(Green, radius = 146f, center = Offset(1342f, 310f))

            // "e"
            drawPath(ePath, textColor)

            // "l" stem
            drawRect(textColor, topLeft = Offset(2144f, 40f), size = Size(256f, 736f))

            // "i" (right): stem + red dot
            drawRect(textColor, topLeft = Offset(2448f, 312f), size = Size(256f, 464f))
            drawCircle(Red, radius = 146f, center = Offset(2574f, 146f))

            // "Q" + blue dot
            drawPath(qPath, textColor)
            drawCircle(Blue, radius = 146f, center = Offset(3342f, 630f))
        }
    }
}

/** Dark mode version of full IntelIQ logo (White text on dark background) */
@Composable
fun InteliqLogoDark(
    modifier: Modifier = Modifier
) {
    InteliqLogo(
        modifier = modifier,
        isDarkTheme = true
    )
}

@Composable
fun IqLogo(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    textColor: Color = if (isDarkTheme) Color.White else Dark,
    iDotColor: Color = Red,
    qDotColor: Color = Blue
) {
    Canvas(modifier = modifier.aspectRatio(IQ_W / IQ_H)) {
        val s = minOf(size.width / IQ_W, size.height / IQ_H)
        val drawWidth = IQ_W * s
        val drawHeight = IQ_H * s
        val offsetX = (size.width - drawWidth) / 2f
        val offsetY = (size.height - drawHeight) / 2f

        withTransform({
            translate(left = offsetX, top = offsetY)
            scale(scaleX = s, scaleY = s, pivot = Offset.Zero)
        }) {
            // "i" (left): stem + yellow dot
            drawRect(textColor, topLeft = Offset(16f, 312f), size = Size(256f, 464f))
            drawCircle(iDotColor, radius = 146f, center = Offset(146f, 146f))

            // "Q" + blue dot shifted left to sit next to "i"
            withTransform({
                translate(left = 356f - 2744f)
            }) {
                drawPath(qPath, textColor)
                drawCircle(qDotColor, radius = 146f, center = Offset(3342f, 630f))
            }
        }
    }
}

/** Dark mode version of standalone IQ logo (White text on dark background) */
@Composable
fun IqLogoDark(
    modifier: Modifier = Modifier,
    iDotColor: Color = Red,
    qDotColor: Color = Blue
) {
    IqLogo(
        modifier = modifier,
        isDarkTheme = true,
        iDotColor = iDotColor,
        qDotColor = qDotColor
    )
}

// --- Previews: Light and Dark Mode Versions ---

@Preview(name = "IQ Logo - Light Mode", showBackground = true)
@Composable
private fun IqLogoLightPreview() {
    Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
        IqLogo(modifier = Modifier.width(200.dp))
    }
}

@Preview(name = "IQ Logo - Dark Mode (White/Black Swapped)", showBackground = true)
@Composable
private fun IqLogoDarkPreview() {
    Box(modifier = Modifier.background(Color(0xFF121212)).padding(16.dp)) {
        IqLogoDark(modifier = Modifier.width(200.dp))
    }
}

@Preview(name = "IntelIQ Logo - Light Mode", showBackground = true)
@Composable
private fun InteliqLogoLightPreview() {
    Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
        InteliqLogo(modifier = Modifier.width(600.dp))
    }
}

@Preview(name = "IntelIQ Logo - Dark Mode (White/Black Swapped)", showBackground = true)
@Composable
private fun InteliqLogoDarkPreview() {
    Box(modifier = Modifier.background(Color(0xFF121212)).padding(16.dp)) {
        InteliqLogoDark(modifier = Modifier.width(600.dp))
    }
}
