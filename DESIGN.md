# Design — KMP Client

Companion to `AGENTS.md` / `ARCHITECTURE.md`. This is the visual and
interaction spec for the KMP + Compose Multiplatform client. It should read
as opinionated and specific to this product — a knowledge-tracing platform
where a real model tracks per-skill mastery — not a generic ed-tech shell.

## 1. What we take from each reference, and what we deliberately skip

| App | Borrow | Skip |
|---|---|---|
| **LeetCode** | Problem-list-as-arena: filterable by difficulty/tag/status, a visible attempt history, a post-attempt "solution" panel | Its bare, code-editor-first visual coldness — we're not a pure IDE |
| **Duolingo** | Streaks, a daily goal ring, bite-sized session pacing, immediate correct/incorrect feedback | The mascot-led, candy-saturated illustration style and forced gamification tone on every screen |
| **Brilliant** | Interactive, explorable explanations rather than static text; confidence-forward "try it yourself" framing | Its very cool, minimal-color restraint everywhere — we need room for the mastery-color language below |
| **Khan Academy** | The mastery map / skill tree as primary navigation metaphor, energy-points-style progress framing | The slightly dated card-grid dashboard and generic blue-green brand |
| **PW (Physics Wallah)** | Dense, exam-prep information hierarchy (batch/course structure, doubt-resolution entry points) for the instructor/course side | Its live-class-first layout — we're an asynchronous, self-paced practice product |

The thing none of these apps have, and our whole reason to look different: a
**real per-skill hidden state**, not a static progress bar. The UI's job is
to make that legible — mastery is a value that moves, per skill, based on
evidence — not just a percentage that climbs.

## 2. Visual identity

### 2.1 Color

Base palette — cool, ink-and-paper with a vibrant **Green** primary signal, complemented by high-contrast Neobrutalist ink borders and hard offset shadows. The mastery scale is functional (it encodes model output), not decorative, and is strictly tied to DKT thresholds.

| Token | Hex | Role |
|---|---|---|
| `ink` | `#171B2E` | Primary text, high-contrast borders, Neobrutalist hard shadow color on light surfaces |
| `paper` | `#EEF0F5` | Default light-mode background (cool, crisp canvas) |
| `paper-raised` | `#FFFFFF` | Cards, sheets, elevated Neobrutalist containers |
| `signal` | `#10B981` | **Primary brand/action color (Green)** — primary buttons, active nav, key highlights |
| `signal-dim` | `#D1FAE5` | Selected-state backgrounds, subtle green highlights |
| `flame` | `#FF6B4A` | Streak, XP, daily-goal — the warm accent reserved for momentum/urgency states |

#### Palette Generation with `materialKolor`
Dynamic Material 3 theme generation uses `materialKolor` with `PaletteStyle.Vibrant` and `ColorSpec.SpecVersion.SPEC_2025` driven by a Green `SeedColor`:

```kotlin
val SeedColor = Color(0xFF10B981) // Green seed color

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val dynamicThemeState = rememberDynamicMaterialThemeState(
        isDark = isDarkTheme,
        style = PaletteStyle.Vibrant,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        seedColor = SeedColor,
    )
    
    DynamicMaterialExpressiveTheme(
        state = dynamicThemeState,
        motionScheme = MotionScheme.expressive(),
        animate = true,
        content = content,
    )
}
```

Mastery scale (matches the product's own mastery bands — see `API_CONTRACT.md` §4 — so the color a student sees on a skill chip is the same threshold the backend uses):

| Band | Range | Hex | Name |
|---|---|---|---|
| Needs Attention | 0.00–0.30 | `#D95550` | Clay |
| Developing | 0.30–0.50 | `#E1943D` | Amber |
| Intermediate | 0.50–0.70 | `#D9B23C` | Ochre |
| Strong | 0.70–0.85 | `#4C9F70` | Moss |
| Mastered | 0.85–1.00 | `#1F6F54` | Pine |

*Note: The Green primary brand color (`#10B981`) drives actions and navigation, while the mastery scale colors (`Moss` / `Pine`) are reserved strictly for DKT model output.*

Dark mode ("Deep Work"): `ink` and `paper` invert to `#0F1220` background / `#EAEBF2` text; `signal` adjusts to `#34D399` for dark surface contrast; Neobrutalist borders and hard shadows shift to crisp dark ink (`#05070F`) or high-contrast accent outlines (`#171B2E`), maintaining tactile punch without soft gaussian blurs.

### 2.2 Neobrutalist Shadows & Aesthetic Language

IntelIQ adopts a **Neobrutalist design system** characterized by bold typography, high contrast, hard-edged borders, and zero gaussian blur:

- **Hard Offset Shadows**: No blurred drop shadows (`elevation = 0.dp` blur). All shadows are sharp, hard-edged offset blocks (e.g. `4.dp` x / `4.dp` y offset).
- **Light Mode Shadows**: Deep ink (`#171B2E`) hard shadows paired with `2.dp` solid ink borders on `paper-raised` (`#FFFFFF`) surfaces.
- **Dark Mode Shadows**: Pitch-dark ink (`#05070F`) hard shadows with high-contrast borders (`#252940` or `signal`), preserving tactile 3D layering on dark backgrounds (`#0F1220`).
- **Tactile Interaction**: Buttons and interactive cards physically compress on press — translating down/right (e.g. shadow reduces from `4.dp` to `1.dp` or `0.dp`), giving a mechanical, satisfying physical feel.
- **Compose Styles API**: All Neobrutalist properties (hard shadow offsets, borders, background fills, padding, press transforms) are unified and managed via Jetpack Compose Styles API (`androidx.compose.foundation.style.Style`).

### 2.3 Type

- **Display/headline — Fraunces** (variable serif). Used for streak/XP numerals, section headers on the skill map, and post-quiz score reveals.
- **UI/body — IBM Plex Sans.** Everything else: nav labels, question text, buttons, body copy.
- **Data/mono — IBM Plex Mono.** Reserved for data: difficulty scores, timers, streak-day counts, mastery percentages, skill IDs.

Line length caps at ~70 characters for body copy; type scale follows a 1.25 ratio from a 15px UI base.

### 2.4 Layout concept

Left-aligned, non-centered layout built for high-frequency study sessions:

```
Mobile (primary)              Desktop (secondary)
+-------------------+         +------+------------------+--------+
| Streak · XP  (top)|         | Nav  |   Main content    | Detail |
+-------------------+         | rail |   (same as mobile |  panel |
|                   |         |      |    center column) | (skill |
|   Screen content  |         |      |                    | info,  |
|                   |         |      |                    | stats) |
+-------------------+         +------+------------------+--------+
| Home Path Practice |
| Progress  Profile  |
+---------------------+
```

## 3. Navigation

Five-item bottom nav (mobile) / left rail (desktop) mapped 1:1 to `presentation/` packages:

| Nav item | Maps to | Primary job |
|---|---|---|
| **Home** | `dashboard` | Continue-learning card, streak/daily-goal, today's recommendation |
| **Path** | `courses` | Khan-Academy-style mastery map — skills as connected nodes |
| **Practice** | `quiz` | LeetCode-style filterable problem/question arena |
| **Progress** | `progress` | Per-skill mastery, trend charts, attempt history |
| **Profile** | `profile` | Stats, achievements, settings |

Persistent top bar contains the streak flame + XP counter across all five tabs.

## 4. Screen inventory

1. **Splash** — brief logo fade.
2. **Onboarding / placement check** — short adaptive diagnostic for KT cold-start.
3. **Auth** — login/register/forgot-password with one primary green action per screen.
4. **Home (Dashboard)** — continue-learning card, streak ring, "Recommended next" (`GET /recommendations/next`), recent mastery movers.
5. **Path (Skill Map)** — skills as nodes connected by prerequisite edges with Neobrutalist border outlines and live mastery-band fills.
6. **Course / Topic detail** — course overview, topic list, entry point into Practice.
7. **Practice (Arena)** — LeetCode-style list with Neobrutalist row cards, difficulty/skill tags, and filter controls.
8. **Question** — question prompt, crisp input cards, timer in Plex Mono.
9. **Answer feedback** — immediate correct/incorrect state, explorable explanation panel, before→after mastery-band chip delta.
10. **Progress (Analytics)** — per-skill mastery list/chart, attempt history, response-time trend.
11. **Recommendation detail** — recommendation explanation (`reason`) + start action.
12. **Achievements** — Neobrutalist badge cards on Profile.
13. **Leaderboard (optional, cohort-scoped)** — weekly, resettable cohort ranking.
14. **Profile / Settings** — account, notification preferences, theme toggle, privacy settings.

## 5. Core UI components

| Component | Behavior / Neobrutalist Styling |
|---|---|
| **Mastery chip** | Skill name + band color + numeric mastery in Plex Mono; `2.dp` ink border, `2.dp` hard shadow offset. |
| **Skill node (Path)** | Circular node, mastery-band fill, `3.dp` ink outline, hard `4.dp` offset shadow; locked nodes render desaturated with lock glyph. |
| **Streak ring** | Daily-goal ring in `flame`, numeral in Fraunces at center; fills on session completion. |
| **Question card** | Neobrutalist container with `2.dp` ink border and `4.dp` hard shadow offset in light/dark mode. |
| **Difficulty badge** | Small 3-tier badge (Easy/Medium/Hard) in Plex Mono with thick border outline. |
| **Explanation panel** | Expands under feedback state; includes interactive worked steps. |
| **Recommendation card** | Question preview + reason string + Green primary start button. |
| **Progress trend chart** | Line/area chart per skill over time with mastery-band line color. |
| **Leaderboard row** | Neobrutalist card row with rank, avatar, XP, rank-change indicator. |
| **Empty/error states** | Specific, contextual guidance wrapped in Neobrutalist bordered containers. |

## 6. Component Styles Architecture & Jetpack Compose Styles API (`@styles` skill)

All UI components integrate the **Jetpack Compose Styles API** (`androidx.compose.foundation.style.Style`), defining unified Neobrutalist styling tokens and state-driven behavior in `ComponentStyles.kt`.

### 6.1 Prerequisites & Compiler Configuration

- `compileSdk` version 37+
- `androidx.compose.foundation:foundation` version `1.12.0-alpha01`+
- Opt-in to Experimental Foundation Style API:
```kotlin
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=androidx.compose.foundation.style.ExperimentalFoundationStyleApi")
    }
}
```

### 6.2 `ComponentStyles.kt` Setup

Neobrutalist style definitions encapsulate hard shadows, thick borders, colors, and press states:

```kotlin
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.unit.dp

object ComponentStyles {

    /** Neobrutalist Card Style with hard offset shadow and thick border */
    val cardStyle: Style = Style {
        background(colors.surface)
        shape(shapes.medium)
        border(width = 2.dp, color = colors.onBackground)
        // Hard offset shadow without gaussian blur
        shadow(
            elevation = 0.dp,
            offsetY = 4.dp,
            offsetX = 4.dp,
            color = colors.onBackground
        )
        padding(16.dp)
    }

    /** Neobrutalist Primary Green Button Style with tactile press animation */
    val primaryButtonStyle: Style = Style {
        background(colors.primary) // Green (#10B981)
        shape(shapes.small)
        border(width = 2.dp, color = colors.onBackground)
        shadow(
            elevation = 0.dp,
            offsetY = 4.dp,
            offsetX = 4.dp,
            color = colors.onBackground
        )
        minWidth(120.dp)
        minHeight(48.dp)
        textStyle(typography.labelLarge)

        // Pressed state: physically compresses into the shadow
        pressed {
            shadow(
                elevation = 0.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                color = colors.onBackground
            )
        }

        // Disabled state: muted background, flat shadow
        disabled {
            background(colors.surfaceVariant)
            shadow(elevation = 0.dp, offsetY = 0.dp, offsetX = 0.dp, color = colors.onBackground)
        }
    }

    /** Neobrutalist Skill Mastery Chip Style */
    val masteryChipStyle: Style = Style {
        shape(shapes.extraSmall)
        border(width = 1.5.dp, color = colors.onBackground)
        shadow(
            elevation = 0.dp,
            offsetY = 2.dp,
            offsetX = 2.dp,
            color = colors.onBackground
        )
        padding(horizontal = 8.dp, vertical = 4.dp)
    }
}

// Theme extensions on StyleScope for token resolution
val StyleScope.colors: ColorScheme
    @Composable get() = MaterialTheme.colorScheme

val StyleScope.typography: Typography
    @Composable get() = MaterialTheme.typography

val StyleScope.shapes: Shapes
    @Composable get() = MaterialTheme.shapes
```

### 6.3 Applying Styles to Components

Custom components accept a `style: Style = Style` parameter and apply `Modifier.styleable()`:

```kotlin
@Composable
fun NeobrutalistButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
    }

    Row(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            )
            .styleable(
                state = styleState,
                defaultStyle = ComponentStyles.primaryButtonStyle,
                style = style
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
```

## 7. Motion

One deliberate moment per major action, augmented by Neobrutalist physical press mechanics:

- **Button / Card Physical Press**: Instant 1-frame downward/rightward translation (`4.dp` offset → `1.dp` offset) on press, returning on release.
- **Streak ring fill** on session completion — celebratory daily beat.
- **Mastery-band transition** — brief color animation when a skill crosses a band threshold (Developing → Intermediate).
- **Correct/incorrect answer** — quick state change on the question card itself.

## 8. Accessibility

- Mastery bands carry explicit labels (`Needs Attention`, `Developing`, …) alongside colors.
- Minimum touch target 44×44dp throughout the Practice list and Path nodes.
- High contrast guaranteed by Neobrutalist `2.dp` solid borders on all cards and interactive controls in both light and dark modes.
- Timer on Question screen is informational and never auto-submits.

## 9. What to actively avoid

- No soft gaussian drop shadows (`Modifier.shadow(elevation = 8.dp)`) — use hard Neobrutalist offset shadows exclusively.
- No infinite streak-guilt mechanics (streak freezes/insurance upsells).
- No generic SaaS-dashboard card grid on Home.
- No overload of the green primary color for non-brand actions — keep mastery bands distinct and semantic.
