package com.techbrains.inteliq.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import com.techbrains.inteliq.presentation.components.LottieLoader
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.components.NeobrutalistButton
import com.techbrains.inteliq.presentation.components.NeobrutalistButtonVariant
import com.techbrains.inteliq.presentation.components.NeobrutalistTextField
import com.techbrains.inteliq.presentation.theme.IntelIQTheme
import inteliq.app.shared.generated.resources.Res
import inteliq.app.shared.generated.resources.splash
import org.jetbrains.compose.resources.painterResource

private fun Color.isDark(): Boolean {
    val luminance = 0.2126f * red + 0.7152f * green + 0.0722f * blue
    return luminance < 0.5f
}

@Composable
fun AuthScreen(
    state: AuthUiState,
    onIntent: (AuthIntent) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    onToggleDarkTheme: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.isDark()
    val screenBgColor = if (isDark) Color(0xFF009382) else Color(0xFFF3E643)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(screenBgColor)
            .statusBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        val viewportWidth = maxWidth
        val isWideScreen = viewportWidth >= 720.dp

        if (isWideScreen) {
            // Wide Screen Layout: Left Branding Pane + Right Full-Height Form Pane (no right margin/padding)
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CanvasBrandingPane(
                    isWideScreen = true,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(24.dp)
                )
                AuthFormPane(
                    state = state,
                    onIntent = onIntent,
                    isWideScreen = true,
                    viewportWidth = viewportWidth,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        } else {
            // Compact / Mobile Screen Layout: Vertical static panes; form pane expands smoothly on toggle
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CanvasBrandingPane(
                    isWideScreen = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                AuthFormPane(
                    state = state,
                    onIntent = onIntent,
                    isWideScreen = false,
                    viewportWidth = viewportWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Top Header Theme Toggle for Desktop and Web
        if (onToggleDarkTheme != null && isWideScreen) {
            Surface(
                onClick = onToggleDarkTheme,
                shape = RoundedCornerShape(24.dp),
                color = colors.surface.copy(alpha = 0.92f),
                border = BorderStroke(2.dp, if (isDark) colors.outline else Color(0xFF171B2E)),
                shadowElevation = 0.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 32.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isDarkTheme) "Dark" else "Light",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Left/Top Branding Pane:
 * Canvas with dotted background, corner geometric shapes, logo, and slogan (NO white window container).
 */
@Composable
fun CanvasBrandingPane(
    isWideScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.isDark()

    val canvasBg = if (isDark) Color(0xFF009382) else Color(0xFFF3E643)
    val shapeBlue = if (isDark) Color(0xFF2563EB) else Color(0xFF3897F0)
    val shapeRed = if (isDark) Color(0xFFDC2626) else Color(0xFFFF2A2A)
    val strokeColor = if (isDark) colors.outline else Color(0xFF171B2E)
    val dotColor =
        if (isDark) colors.onBackground.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(canvasBg),
        contentAlignment = Alignment.Center
    ) {
        // Dotted grid & corner geometry canvas layer
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw Dotted Background Grid
            val dotSpacing = 20.dp.toPx()
            val dotRadius = 1.8.dp.toPx()

            var x = 10.dp.toPx()
            while (x < width) {
                var y = 10.dp.toPx()
                while (y < height) {
                    drawCircle(
                        color = dotColor,
                        radius = dotRadius,
                        center = Offset(x, y)
                    )
                    y += dotSpacing
                }
                x += dotSpacing
            }

            // 2. Corner Geometric Shapes
            // Top-Right Blue Circle
            val trRadius = (width * 0.22f).coerceIn(50.dp.toPx(), 160.dp.toPx())
            val trCenter = Offset(width - trRadius * 0.2f, trRadius * 0.2f)
            drawCircle(color = shapeBlue, radius = trRadius, center = trCenter)
            drawCircle(
                color = strokeColor,
                radius = trRadius,
                center = trCenter,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Bottom-Left Red Circle
            val blRadius = (width * 0.25f).coerceIn(55.dp.toPx(), 180.dp.toPx())
            val blCenter = Offset(blRadius * 0.2f, height - blRadius * 0.2f)
            drawCircle(color = shapeRed, radius = blRadius, center = blCenter)
            drawCircle(
                color = strokeColor,
                radius = blRadius,
                center = blCenter,
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        // Branding Content directly on Canvas
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val logoWidth = if (isWideScreen) 420.dp else 300.dp
            Image(
                painter = painterResource(Res.drawable.splash),
                contentDescription = "IntelIQ Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(logoWidth)
            )
        }
    }
}

/**
 * Right/Bottom Auth Form Pane:
 * Static rounded neobrutalist card expanding smoothly on toggle (Sign In vs Sign Up height).
 * Omits Google/GitHub social login buttons and ACCESS heading per explicit spec.
 */
@Composable
fun AuthFormPane(
    state: AuthUiState,
    onIntent: (AuthIntent) -> Unit,
    isWideScreen: Boolean = false,
    viewportWidth: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    var rememberMe by remember { mutableStateOf(true) }
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.isDark()
    val strokeColor = if (isDark) colors.outline else Color(0xFF000000)
    val checkActiveBg = if (isDark) colors.primary else Color(0xFFF3E643)

    // Calculate column width fraction for wide screen layout (70% - 80% of right pane width).
    // As viewport width increases from 720.dp to 2560.dp, scale the column width fraction smoothly from 80% to 70%.
    val columnWidthFraction = if (isWideScreen && viewportWidth > 0.dp) {
        (0.80f - ((viewportWidth - 720.dp) / (2560.dp - 720.dp)) * 0.10f).coerceIn(0.70f, 0.80f)
    } else if (isWideScreen) {
        0.75f
    } else {
        1f
    }

    val formShape = if (isWideScreen) {
        RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp, topEnd = 8.dp, bottomEnd = 8.dp)
    } else {
        RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp, bottomStart = 36.dp, bottomEnd = 36.dp)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isWideScreen) Modifier.fillMaxHeight() else Modifier),
        shape = formShape,
        color = colors.surface,
        border = BorderStroke(2.5.dp, strokeColor)
    ) {
        Box(
            modifier = if (isWideScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(columnWidthFraction)
                    .then(if (isWideScreen) Modifier.fillMaxHeight() else Modifier)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = if (isWideScreen) 32.dp else 28.dp,
                        vertical = if (isWideScreen) 36.dp else 28.dp
                    ),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = if (isWideScreen) Arrangement.Center else Arrangement.Top
            ) {
                // Error Banner
                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    state.errorMessage?.let { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = colors.errorContainer,
                            border = BorderStroke(2.dp, colors.error)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg,
                                    color = colors.onErrorContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { onIntent(AuthIntent.ClearError) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Error",
                                        tint = colors.onErrorContainer,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (state.isRegistering) "Create Account" else "Log in",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        color = colors.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Full Name field (Registering mode)
                AnimatedVisibility(
                    visible = state.isRegistering,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    Column {
                        NeobrutalistTextField(
                            value = state.name,
                            onValueChange = { onIntent(AuthIntent.NameChanged(it)) },
                            label = "FULL NAME",
                            placeholder = "Enter your full name",
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = colors.onSurface
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Email field
                NeobrutalistTextField(
                    value = state.email,
                    onValueChange = { onIntent(AuthIntent.EmailChanged(it)) },
                    label = "EMAIL",
                    placeholder = "student@inteliq.edu",
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = colors.onSurface
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                NeobrutalistTextField(
                    value = state.password,
                    onValueChange = { onIntent(AuthIntent.PasswordChanged(it)) },
                    label = "PASSWORD",
                    placeholder = "••••••••",
                    isPassword = true,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = colors.onSurface
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me & Forgot Password Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (rememberMe) checkActiveBg else colors.surface)
                                .border(2.dp, strokeColor, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rememberMe) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isDark) colors.onPrimary else strokeColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REMEMBER ME",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                    }
                    Text(
                        text = "FORGOT PASSWORD?",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        modifier = Modifier.clickable { /* Forgotten password handler */ }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Submit Button
                NeobrutalistButton(
                    onClick = { onIntent(AuthIntent.SubmitAuth) },
                    enabled = !state.isLoading && state.email.isNotBlank() && state.password.isNotBlank(),
                    variant = NeobrutalistButtonVariant.Primary,
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (state.isLoading) {
                        LottieLoader(
                            size = 32.dp,
                            isDark = isDark
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (state.isRegistering) "CREATE ACCOUNT" else "SIGN IN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LocalContentColor.current
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = LocalContentColor.current,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle Login / Register
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isRegistering) "Already have an account?" else "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Row(
                        modifier = Modifier.clickable { onIntent(AuthIntent.ToggleAuthMode) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.isRegistering) "Sign in" else "Create an account",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Debug Skip Auth Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        modifier = Modifier.size(16.dp),
                        enabled = true,
                        onClick = { onIntent(AuthIntent.DebugSkipAuth) },
                        colors = IconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Black,
                        )
                    ) {
                        Icon(
                            Icons.Default.BugReport,
                            contentDescription = "Debug mode",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    IntelIQTheme {
        AuthScreen(
            state = AuthUiState(
                isRegistering = false,
                email = "user@example.com"
            ),
            onIntent = {},
            modifier = Modifier
        )
    }
}

