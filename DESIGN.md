# Design — KMP Client

Companion to `AGENTS.md` / `ARCHITECTURE.md`. This is the visual and
interaction spec for the KMP + Compose Multiplatform client. It should read
as opinionated and specific to this product — a knowledge-tracing platform
where a real model tracks per-skill mastery — not a generic ed-tech shell.

> **Source of truth:** the five AXIOM mockups (Home, Learn, Discover,
> Practice, Profile) for **layout, content, hierarchy and component
> inventory**. Where this document and a mockup disagree on those, the mockup
> wins *unless* the item is listed in §10 (mockup issues we are deliberately
> not copying). Screens with no mockup yet (Question, Answer feedback, Auth,
> Onboarding, dark mode) are marked **[no mockup]** and must be restyled to
> match this language when designed.
>
> **Visual-language revamp (supersedes the mockups' surface styling).** The
> mockups are neobrutalist: thick ink borders, hard offset shadows, flat
> paper. That read as informal for a product whose pitch is a real
> knowledge-tracing model, so the surface language is replaced by
> **a mesh-gradient backdrop + Haze glass surfaces + ProShape continuous
> corners**, kept deliberately quiet. On *surface styling* (borders, shadows,
> fills, radii, press mechanics) this document wins over the mockups; a
> restyled mockup pass is tracked as D5 in §10. The color system is
> unchanged and stays dynamic (§2.1).

## 1. What we take from each reference, and what we deliberately skip

| App | Borrow (and where it shows up) | Skip |
|---|---|---|
| **LeetCode** | Filterable problem table with status / acceptance / difficulty / tags (**Practice**); "Pick random"; per-difficulty solve counts (**Profile**); submission heatmap | Its bare, code-editor-first coldness |
| **Duolingo** | Winding lesson path with a current-node call-out (**Learn**); streak, daily goal ring, daily quests, weekly league (**Home**, **Profile**); bite-sized sessions | The mascot-led, candy-saturated illustration style; monetised urgency (see §9) |
| **Brilliant** | Interactive/visual "core theorem" panel next to each lesson and on the daily problem (**Home**, **Learn**) | Its very cool, minimal-color restraint — we need room for the mastery-color language |
| **Khan Academy** | Unit → chapter → node hierarchy with stars and a unit-level progress track (**Learn**); course catalog with levels (**Discover**) | The dated card-grid dashboard and generic blue-green brand |
| **PW (Physics Wallah)** | Dense exam-prep hierarchy: sheets, community tracks, syllabus links, downloadable PDFs/cheatsheets (**Discover**, **Practice**) | Its live-class-first layout — we are asynchronous and self-paced |

The thing none of these apps have, and our whole reason to look different: a
**real per-skill hidden state**, not a static progress bar. The UI's job is
to make that legible — mastery is a value that moves, per skill, based on
evidence — not just a percentage that climbs. Every screen below says where
that model output surfaces.

## 2. Visual identity

### 2.1 Color

A quiet, tone-matched backdrop, soft ink, and one **volt-lime** action color. Lime is the
brand/action signal; **green is no longer the primary** — it now means
"success / progress / easy". The mastery scale is functional (it encodes
model output), not decorative, and is strictly tied to DKT thresholds.
Hex values below were sampled from the mockups — confirm against the design
source before freezing tokens.

| Token | Hex | Role |
|---|---|---|
| `ink` | `#14172B` | Text and icons on light surfaces; hairlines at 10–12% alpha. No longer used for thick borders or shadows |
| `paper` | `#FBFAF6` | Base of the page and mesh (maps to `colorScheme.background`) |
| `paper-raised` | `#F6F4EE` | **Solid** surfaces: dense cards, table containers, nested tiles (maps to `surfaceContainer`; warm off-white, not pure white) |
| `paper-sunken` | `#E9E6DD` | Inset tracks, empty progress bars, disabled/locked fills, table header (maps to `surfaceContainerHighest`) |
| `signal` | `#E4FF1A` | **Primary action/brand (volt-lime)** — primary CTAs, active nav item, "You" rows, current node, selected chips. Always carries `ink` text, never used as text color |
| `signal-soft` | `#F4FFA0` | Hero-card glow, subtle highlight panels, "NEXT" lesson row |
| `signal-stroke` | `ink` @ ~50% | 1.dp definition stroke on lime fills **in light mode only**, so the fill keeps a ≥ 3:1 boundary against light surroundings (lime on near-white is ~1.2:1). Starting value — verify. Not needed in dark mode |
| `flame` | `#FF8A00` | Streak, POTD, XP-boost, Hot tag — momentum/urgency |
| `sky` | `#3DB8FF` | Secondary action (Pick Random, Resume, Continue), Next-Milestone tag, Linear-Algebra-type tracks |
| `grape` | `#A64CFF` | Competitive layer: Arena, leagues, Elo, Diamond tier |
| `rose` | `#FF3D6E` | Hard difficulty, Unit Boss, destructive/error |
| `mint` | `#12D66B` | Success, progress fill, Easy difficulty, correct answer |

Tag/pill backgrounds use a ~20% tint of the accent with the accent's dark
shade for text (e.g. orange pill for `POTD`, purple for `Arena`, green for
`Sheet`, sky for `Tree`).

Mastery scale (matches the product's own mastery bands — see
`API_CONTRACT.md` §4 — so the color a student sees on a skill chip is the
same threshold the backend uses):

| Band | Range | Hex | Name |
|---|---|---|---|
| Needs Attention | 0.00–0.30 | `#D95550` | Clay |
| Developing | 0.30–0.50 | `#E1943D` | Amber |
| Intermediate | 0.50–0.70 | `#D9B23C` | Ochre |
| Strong | 0.70–0.85 | `#4C9F70` | Moss |
| Mastered | 0.85–1.00 | `#1F6F54` | Pine |

**Separation rule (new):** mockups use green/amber/red for *difficulty*
(Easy/Medium/Hard) and a rainbow of per-topic colors for topic bars. Both
collide with the mastery scale. Therefore:

- Difficulty is shown **only** as a labelled pill using `mint` / `flame-soft` / `rose` tints — never as a bar fill that could be read as mastery.
- Anything that represents **model mastery** (skill nodes, Topic Mastery bars, mastery chips, trend lines) uses the band colors above, chosen by value, and always includes the band label or number.
- Progress that is *completion* (lessons done, quests solved, roadmap %) uses `mint`.

**Semantic colors on glass.** Anything whose hue carries meaning — mastery
bands, difficulty pills, accent tags, mint/rose states — is drawn as an
**opaque or tint-on-solid fill, never as glass over the mesh**, because a
backdrop shifts hue. Glass holds neutrals, text and the lime action only.

**The palette stays dynamic.** Every neutral, container and mesh color is read
from the `materialKolor` scheme at runtime (see below); the hex values in the
table are the light-mode reference sampled from the mockups, not constants to
hard-code. Accent tokens and mastery bands stay fixed.

#### Palette generation with `materialKolor`
Material 3 theme generation still uses `materialKolor` with
`PaletteStyle.Vibrant` and `ColorSpec.SpecVersion.SPEC_2025`, seeded from the
lime. **Implementation note:** a tonal palette generated from lime yields a
dark olive `primary` in light mode, not the loud lime in the mockups. Seed
the scheme for neutrals/containers, then **pin** `primary = signal`,
`onPrimary = ink` (and the accent tokens above) as explicit overrides on the
generated scheme.

```kotlin
val SeedColor = Color(0xFFE4FF1A) // volt-lime seed

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
    // + app-level AccentTokens (signal, flame, sky, grape, rose, mint) provided
    //   via a CompositionLocal; primary/onPrimary pinned as described above.
}
```

**Dark mode ("Deep Work") [no mockup — proposal]:** base `#0F1220`, solid
surfaces `#171B2E`, text `#EAEBF2`. The mesh moves to the dark end of its tonal
range (§2.2) and glass uses Haze's dark response. `signal` stays lime with
`ink` text and needs no stroke. Hairlines are `#EAEBF2` at 10–12%. Accent tints
darken ~30%. Needs a mockup pass before build.

### 2.2 Surface & shape language — mesh, glass, continuous corners

Quiet and precise. Depth comes from **material and tone**, not outlines or
offset shadows.

**Three layers**

1. **Backdrop — mesh gradient.** One full-screen mesh behind everything, from
   the app's own mesh-gradient library, and the *only* `hazeSource`. Its colors
   come from the dynamic scheme (`background`, `primaryContainer`,
   `secondaryContainer`, `tertiaryContainer`, `surfaceContainer*`), low chroma.
   At most one `signal-soft` glow point at ≤ 35% alpha, never under body text.
   Accent tokens and mastery colors never appear in the mesh. Static by
   default; optional slow drift (§7).
2. **Surfaces — glass or solid.** See the rules below.
3. **Content — text, icons, chips, charts.** Always legible without relying on
   what is behind it.

**Mesh legibility rule.** Keep the mesh inside a narrow tonal band (starting
point: tone ~90–97 in light, ~8–18 in dark) so `onSurface` text over glass
passes 4.5:1 at the worst point. Enforce it with a test that samples the mesh
on a grid, applies the glass tint, and asserts contrast, rather than by eye.

**Glass vs solid**

- **Glass** (`GlassStyle.regular` only — never `clear` behind text): top bar,
  sidebar / bottom bar, hero cards, a focal side-column panel, sheets and
  dialogs, the current-node tooltip.
- **Solid** (`paper-raised` + 1.dp hairline): everything repeated or dense —
  table rows and containers, catalog and community cards, stat tiles, the
  question card, nested panels. Haze notes that glass can be costly in dense
  scrolling layouts, and solid keeps text contrast independent of the backdrop.
- **One glass level.** Anything nested inside a glass surface is a solid tonal
  fill, never glass-on-glass.
- **Budget.** Starting guideline: no more than ~6 glass surfaces visible at
  once on Compact. Measure on a low-end device and adjust (D6).
- **Merge related controls.** The top-bar chip cluster and segmented controls
  are one `hazeGlassContainer` so they read as a single object.
- **No extra borders or shadows on glass.** The material supplies its own rim
  and specular edge. Hairlines belong to solid surfaces only. There are no drop
  shadows anywhere.
- **Fallbacks.** On renderers without refraction, Haze keeps tint, rounded
  shape and rim and drops refraction and blur. Never let meaning depend on
  refraction.

**Corners — ProShape (continuous)**

| Tier | Radius | Used for |
|---|---|---|
| Hero | `32.dp` | Hero cards, sheets, dialogs |
| Card | `24.dp` | Standard cards, bars, table containers (rows stay square inside) |
| Tile / nested | `20.dp` | Tiles, nested panels. **Concentric rule:** inner radius = outer radius − padding between them |
| Pill | half the fixed height | Buttons (48.dp → 24.dp), chips and tags (28.dp → 14.dp), search, stat pills |

All radii live in one `Radii` object and are consumed through `AppShapes`
(§6.4), so the corner library can be swapped in one file. Glass surfaces take
the same radii as `RoundedCornerShape`, because Haze's glass `shape` is
`RoundedCornerShape`-typed (see §6.7, risk 1).

**States**

- **Selected = solid lime pill with `ink` text** (active nav item, active tab).
  Active *filter* chip uses a solid `onSurface` fill with `surface` text
  instead (see §5). In light mode lime fills carry `signal-stroke`.
- **Press:** glass controls use Haze's press response (`scale(0.98f)` plus
  lighting). Solid controls darken their fill ~12%. No translate, no shadow
  collapse.
- **Decoration:** a large card may carry one oversized glyph at ≤ 8% alpha in a
  corner, never behind text that needs to be read. The mesh provides the glow,
  so cards carry no glow of their own, except the hero's `signal-soft` glow.
- **Compose Styles API:** non-glass styling is expressed through
  `androidx.compose.foundation.style.Style`; glass is applied with Haze
  modifiers (§6).

### 2.3 Type

The mockups moved away from a serif display face to a tight, heavy grotesque
plus a heavy use of mono for labels.

- **Display / headline — a tight, bold grotesque** (Space Grotesk or Bricolage Grotesque; pick one and verify against the mockup). Page titles, hero titles, card titles, big numerals (142, 2,140, 18). Tight tracking (−2% to −3%) at display sizes. *Replaces Fraunces.*
- **UI / body — IBM Plex Sans** (or the grotesque's text cut). Descriptions, nav labels, buttons, question text.
- **Data / labels — mono** (IBM Plex Mono or JetBrains Mono). Eyebrows (`UNIT 03 · PATH TO MASTERY`), tags, stat captions, timers, XP, percentages, table headers, nav tag-pills. Eyebrows and table headers are **UPPERCASE with +6–8% tracking**.
- Scale: 1.25 ratio from a 15sp base. **Floor of 11sp** for any mono label (several mockup labels are ~9–10sp and fail legibility). Body line length caps at ~70 characters.
- **Math is typeset, not printed.** Formulas such as `P(E) = 1 − 365!/…` and `f(g(x))` must render as real math (see §10); raw `$…$` / `$$…$$` is never shown to a student.

**Revamp adjustments [proposal — confirm against the restyled mockups].**
The grotesque + Plex + mono families stay. To read calmer and more
professional: display weight drops from heavy to SemiBold (600); mono is kept
for *data* (numbers, timers, XP, percentages, table headers) and eyebrows
drop to +4% tracking; all-caps stays only on eyebrows and table headers, not on
button labels (hero CTAs excepted). The 11sp floor is unchanged.

### 2.4 Layout concept

Left-aligned, card-based, built for high-frequency study sessions. The
mockups show the expanded layout; compact is derived.

```
Expanded (mockups)                            Compact (mobile, primary use)
+----------+--------------------------------+ +------------------------+
| Logo     | Top bar: search ⌘K | streak |  | | Top bar: streak · XP   |
| AXIOM    |  XP | goal ring | theme | lvl  | +------------------------+
|          +----------------+---------------+ |                        |
| Home     |  Main column   | Side column   | |   Screen content       |
| Learn    |  (hero, lists) | (quests, league| |   (side column stacks  |
| Discover |                |  detail panel)| |    under main column)  |
| Practice |                |               | |                        |
| Profile  |                |               | +------------------------+
|          |                |               | | Home Learn Discover    |
| [Roadmap |                |               | | Practice Profile       |
|  progress]                |               | +------------------------+
+----------+----------------+---------------+
```

- Content sits on a 12-col grid inside a max-width container; **main column ≈ 2/3, side column ≈ 1/3**. Home, Learn (tree + lesson panel), Practice (header card + completion card) and Profile (heatmap + league) all use this split.
- Breakpoints: **Compact** (<600dp) bottom bar, single column, side-column cards stack beneath main content; **Medium** (600–1000dp) icon-only rail, single column with 2-up grids; **Expanded** (≥1000dp) labelled sidebar with tag-pills, two-column content.
- Sidebar and top bar are **glass surfaces** floating over the mesh (inset 12.dp from the window edge on Medium/Expanded, edge-to-edge on Compact), separated from content by material, not by rules. Both stay fixed while content scrolls beneath them. Make the scroll container a `hazeSource` above the backdrop so content blurs under the bars.

## 3. Navigation

Five items, bottom bar (compact) / sidebar (expanded). Each item has an icon,
a label, and — in the expanded sidebar — a **right-aligned mono tag-pill**
naming what lives inside. The active item is a solid lime pill with `ink` text (plus
`signal-stroke` in light mode), no border, no shadow.

| Nav item | Tag-pill | Maps to | Primary job |
|---|---|---|---|
| **Home** | `POTD` (orange) | `dashboard` | Greeting, daily problem, active-track card, daily quests, weekly rhythm, league snapshot, game modes |
| **Learn** | `Tree` (sky) | `courses` (path) | Unit tabs + skill tree + lesson detail panel |
| **Discover** | `Arena` (grape) | `courses` (catalog) | Course directory, featured/popular, full catalog, community tracks |
| **Practice** | `Sheet` (green) | `quiz` | Problem sheets as a filterable table; live drills; cheatsheets |
| **Profile** | `Euler` (neutral) | `profile` + `progress` | Identity, stats, heatmap, league, difficulty & topic mastery, badges |

**Change from previous spec:** the old **Path / Progress** tabs are now
**Learn / Discover**, and **Progress** (per-skill mastery, trend, attempt
history) has been **folded into Profile**. `presentation/progress` therefore
becomes a sub-feature of `profile`, and `presentation/courses` serves both
Learn and Discover. The tag-pill on Profile shows the learner's current
**title** (e.g. `Euler`), not a feature name.

**Top bar (persistent, all screens):** search pill with `⌘K` hint (theorems,
formulas, sheets) · streak chip (flame + `18 Days`) · XP chip (gem + `2,450
XP`) · daily-goal chip (ring + `4/5 Goal`) · light/dark toggle · **Level
block** (`Level 14` + mathematician title, role label `Apprentice`, avatar).
On compact, collapse to streak + XP + avatar; search moves behind an icon.

**Sidebar footer:** *Roadmap Progress* card — mono caption, `72%`, chunky
mint bar, `142 / 198 Quests Solved`.

## 4. Screen inventory

Screens 1–3 are unchanged from the previous spec. **[no mockup]** screens
should be built with the surface language in §2.2.

1. **Splash** — brief logo fade. The wordmark is the pixel-style logotype with four colored dots (yellow, red, green, blue) — keep it as an asset, not live text.
2. **Onboarding / placement check [no mockup]** — short adaptive diagnostic for KT cold-start.
3. **Auth [no mockup]** — login / register / forgot-password; one lime primary action per screen.
4. **Home** — see §4.1.
5. **Learn (skill tree)** — see §4.2.
6. **Discover (course directory)** — see §4.3.
7. **Practice (sheets)** — see §4.4.
8. **Profile** — see §4.5 (includes the former Progress analytics).
9. **Question [no mockup]** — prompt in a raised card, typeset math, input cards, timer in mono; timer never auto-submits.
10. **Answer feedback [no mockup]** — immediate correct/incorrect on the card, explorable explanation panel, **before → after mastery-band chip delta**. This is the primary place the DKT update is shown and must not be dropped.
11. **Recommendation detail [no mockup]** — recommendation `reason` + start action.
12. **Achievements** — full badge list (entry from Profile's *Earned Badges* card).
13. **Leaderboard** — full league view (entry from *Weekly League* / *Diamond League* cards).
14. **Settings** — account, notifications, theme, privacy (entry from the Profile gear button).

### 4.1 Home (Dashboard)

Order, top to bottom:

1. **Greeting banner** — lightning icon tile, "Good morning, {name}!", `2X BOOST ACTIVE` pill, one-line prompt; right side: monthly-goal mini bar (`18 / 30 Days`) and Tier tile (`Diamond III`).
2. **Daily Challenge hero (main)** — eyebrow chips (`DAILY CHALLENGE // date`, difficulty + topic chip), reset countdown pill, mono quest id (`QUEST #412 · DISCRETE PARADOXES`), display title, problem statement (typeset), meta pills (`~12 mins`, `+50 XP`, `Streak Saver`, `N Solved Today`), lime CTA **SOLVE DAILY PROBLEM →**. Right: **interactive concept panel** (formula + a small live curve with labelled thresholds and a one-line takeaway). Faint oversized glyph in the corner.
3. **Active Track (main)** — eyebrow `ACTIVE TRACK // ROADMAP`, track title, `Sprint 4 of 6` pill, `Conquered Nodes 14 / 20 Lessons (68%)` mint bar, then a **vertical timeline**: mastered node (struck-through, `+100 XP`, check), **Next Milestone card** (sky tag, difficulty tag, title, description, `CONTINUE JOURNEY` button), locked node (`Unlocks after Lesson 15`); footer link `View Entire Syllabus Tree` + estimated time left.
4. **Daily Quests (side)** — `2/3 Done`; checklist rows with `+XP` right-aligned; completed rows struck through with mint check.
5. **Weekly Rhythm (side)** — `18-Day Streak`; Mon–Sun tiles: completed = `flame` fill + flame glyph, today = lime + bolt, future = dashed lock.
6. **Diamond League (side)** — `TOP 5 TODAY`; ranked rows (avatar, name, flag, daily XP + streak, total); **"You" row is lime**; footer link `View full Diamond ranks (top 30 advance)`.
7. **Game-mode cards (bottom, 2-up)** — *Speed Math Duel (1v1)* with live-queue count and `Find Match`; *Calculus Speedrun* with `+35 XP` and `Start Sprint`.

**Where the model shows up:** the Daily Challenge is editorial (same for
everyone, "problem of the day"). The **Next Milestone card is the
model-driven slot** — it is where `GET /recommendations/next` renders, and
it **must show the `reason` string** (e.g. "Eigenvalue mastery 0.41 —
prerequisite for SVD"). Also restore the previously specified **"Recent
mastery movers"** strip (mastery chips with before→after delta) — it is not
in the mockup and should be added under the Active Track card.

### 4.2 Learn (skill tree)

- **Unit tabs** — pill tabs across the top (`Algebra I`, `Discrete Math & Logic`, `Calculus I–III` *active: lime + unit badge*, `Number Theory`, `Linear Algebra`) with an `XP happy hour` timer pill at the far end (informational only, see §9).
- **Unit hero** — eyebrow chips (`UNIT 03 · PATH TO MASTERY`, `★ 12 / 18 Stars`), display title, one-line description, actions (`Unit Guidebook` with PDF + Cheats tag, lime `Resume Quest`), and a **segmented chapter track** (one segment per chapter: complete = mint, current = lime, upcoming = empty) with `67% Completed`.
- **Skill tree (main, ~60%)** — vertical, winding path (organic S-curve, mint stroke for completed stretches, faint grey for locked). Legend: `Solved · Current · Locked`. Node types:
  | Node | Look |
  |---|---|
  | Mastered | Large circle, medal glyph, small star badge |
  | Solved | Medium dark-green circle with check; double-check for 100% score |
  | **Current** | Black circle, play glyph, **lime ring**, floating black tooltip `START LESSON (+25 XP)`, lime label pill |
  | Locked | Desaturated circle, lock glyph, label `Unlocks after Node N` |
  | Bonus | Lavender circle, gift glyph, `BONUS` tag (e.g. chest, `+150 XP · 10 Gems`) |
  | Unit Boss | Rose-tinted circle, crossed-swords glyph, `UNIT BOSS` tag, `Hard · Final Exam` |
  Every node has a name label pill below it with a mono subline (`Mastered · 3/3 ★`, `Active Quest · Exercise 2/4`).
- **Lesson detail panel (side, ~40%)** — chip row (`CHAPTER 3 · NODE 4`, `8 mins`, `4/7` ring), title, short description (typeset math), **Core Theorem Visualization** (formula in a bordered card with a form-switcher tag, plus a small graph), **Lesson Path** list (`2 of 4 completed`: done rows with check and `+XP`, **NEXT** row in `signal-soft`, locked row), full-width mint CTA **JUMP IN**, a one-line "keeps streak alive" note and `Practice weak skills` link.
- **Below the panel** — a game-mode card (*Calculus Speed Sprint*, `Find Match`) and a **Unit Lore** card (illustration + short historical blurb).

**Where the model shows up (proposal — mockup does not show it):**
- Completed node fill **= mastery band** of its skill (Pine for Mastered … Clay for Needs Attention) with the band name in the node's accessibility label; the gold/green/check treatment becomes an icon overlay, not the fill.
- **Stars derive from mastery band:** ★ Intermediate+, ★★ Strong, ★★★ Mastered. Unit `12 / 18 ★` then summarises model state.
- *Practice weak skills* routes to the lowest-mastery skills in the unit.

### 4.3 Discover (course directory)

- **Directory hero card** — eyebrow chip (`COURSE DIRECTORY & SYLLABI`) + count (`84 Interactive Tracks Available`), display title, description, **search field with ⌘K hint** and lime `Search` button, horizontally scrolling **filter chips** (`All Courses (84)` active = solid ink fill with paper text, then subject chips).
- **Featured & Popular** (3-up) — each card has a **tinted header block** (lime / sky / lavender) holding a large icon tile, a rating pill (`★ 4.9 (2,410)`) and a status badge (`Popular` mint, `Trending` orange, `Advanced` rose), then subject + level tags, title, 3-line description, `hours · enrolled` footer, and a **full-width CTA colored to match the header** (`ENROLL / START TRACK →`). Third card uses the neutral button when no accent applies.
- **Full Course Catalog** (3-up grid, sortable `Sort by: Recommended`) — compact cards: subject tag (colored dot) + `Level n`, title, description, `N Modules · N Quests` pill, then footer `100% Free Access` + `ENROLL FREE`. **In-progress** cards swap the pill for a progress bar (`8/16 Modules · 50%`), show an `IN PROGRESS` lime tag, and the footer becomes `Module 9 Next` + sky `RESUME`.
- **Community Learning Tracks & Micro-Courses** — solid tonal section (not glass) with `Propose a Track` button; 3-up cards with a colored category tag (`Olympiad` rose, `Foundations` grape, `Micro-Track` sky), duration, title, description, author line (name + affiliation), rating, `VIEW SYLLABUS →`.

**Where the model shows up (proposal):** the catalog's default sort,
**Recommended**, is the model-driven sort; add a one-line `reason` on
recommended cards, and a prerequisite-readiness hint on cards the learner is
not ready for.

### 4.4 Practice (sheets)

- **Sheet header card (main)** — eyebrow (`STRIVER × AXIOM CURRICULUM`, `Updated 3 hrs ago`), streak-multiplier pill (`STREAK MULTIPLIER ×2.5`), display title, description, and **sheet tabs** with counts (`A2Z Math Sheet 185` active = ink fill; `Olympiad Math 75`, `Quant Finance Essentials 60`, `Discrete Math Mastery 100`).
- **Curriculum Completion card (side)** — trophy tile, **donut gauge** (`33.8% DONE`), `SOLVED 142 / 420`, acceptance rate, percentile (`Top 12% in Combinatorics`), and Easy / Medium / Hard bars with counts.
- **Search row** — search field (⌘K) + sky `PICK RANDOM` button.
- **Filters** — a `TOPIC:` chip group (active chip = ink fill) and a **segmented difficulty control** (`All (420) · Easy (160) · Medium (195) · Hard (65)`).
- **Problem table** — header strip (`Showing 6 of 420 Questions • Section: …`, sort and column-visibility icons) and columns: **Status** (check / in-progress / empty) · **#** · **Problem title** (`HOT` tag allowed inline) · **Proof / Video** duration pill · **Acceptance %** · **Difficulty** pill · **Key tags** (stacked mono chips) · **Action** icon button. Footer: `Rows per page`, range text, numbered pagination with ink-filled current page.
- **Bottom row (3-up)** — *Mental Math Sprint* (Live Drill, `+150 XP`, `Start Blitz`), *Theorem CheatSheet* (PDF Notes, version + `Download`), *1v1 Math Clash* (online count, Elo range, `Find Match`).

**Compact:** the table collapses to **row cards** (status, number, title on line 1; duration · acceptance · difficulty on line 2; tags on line 3). Title column must never be narrower than ~40% of the row.

**Where the model shows up (proposal):** key-tag chips on each row carry a
small **mastery-band dot** for the learner's current mastery of that skill,
and a `Recommended for you` sort/filter is added next to difficulty. The
`Acceptance %` column is a *population* statistic — label it so it is not
confused with personal mastery.

### 4.5 Profile (identity + analytics)

- **Profile header card** — avatar with verified badge, display name, handle chip, league chip (`TOP 1.5% · DIAMOND LEAGUE`, grape), meta lines (school/club, join date, region), lime `SHARE PROFILE` button and a circular settings button.
- **Stat tiles (4-up)** — `Solved Problems 142 / 420`, `Current Streak 18 Days on Fire` (flame numeral), `Max Record Streak 45 Personal Best`, `Global Elo Rating 2,140` with a rank pill (`Master`, grape numeral). Large display numerals, mono caption above, small caption below.
- **Submission Heatmap (main)** — title, one-line description, year toggle (`2024 | 2023`), summary bar (`412 Submissions in 2024`, active days, current/longest streak), month-labelled grid with a `Mon · Wed · Fri` cadence axis and a `Less → More` legend; 5 mint intensities on `paper-sunken`.
- **Weekly League (side)** — `Resets in 2d 14h`; ranked rows with rank medallion, initials avatar, name, points · tier; **"You" row lime** with `YOU` pill and `+185 XP` delta.
- **Difficulty Mastery & Gauge (main)** — **rename to "Solved by Difficulty"** (it is solve counts, not model mastery). Donut (`142 SOLVED of 420`, `33.8% Overall Completion`) + three tier cards (Easy / Medium / Hard: `85 / 160 · 53%` etc.) and a lime comparison pill (`Beats 89.4% Mathletes`).
- **Earned Badges (side)** — `14 / 32 Badges`; 2-up badge cards (colored circular glyph, name, mono requirement line).
- **Topic Mastery (side)** — `4 Core Domains` pill; per-domain label + `%` + chunky bar. **This is the card that must show real DKT output**: bars use **mastery-band color by value** (not per-topic accent colors), show the band label on tap/hover, and add a trend arrow (↑/→/↓ over 7 days). Tapping a domain opens per-skill mastery, trend chart and attempt history (the former Progress screen).

## 5. Core UI components

| Component | Behavior / styling |
|---|---|
| **Top-bar chip** | Pill on the glass top bar; the chips form one `hazeGlassContainer` cluster (merged surface, no per-chip border). Icon (colored) + two-line mono value (`18` / `Days`). Streak = flame, XP = grape gem, Goal = mint ring |
| **Nav item** | Pill-ish row inside the glass sidebar / bottom bar: icon, label, right-aligned tag-pill. Active = solid lime pill, `ink` text (+ `signal-stroke` in light mode) |
| **Eyebrow chip** | Small pill, UPPERCASE mono, tinted background (lime for context, sky/orange/grape by meaning) |
| **Section / hero card** | **Glass** (`GlassStyles.hero`), `32.dp` radius, no border, no shadow; optional single oversized glyph at ≤ 8% alpha and the hero `signal-soft` glow |
| **Glass bar** (top bar, sidebar, bottom bar) | **Glass** (`GlassStyles.bar`), `24.dp` radius (square on Compact edge-to-edge), content scrolls beneath it |
| **Sheet / dialog** | **Glass** (`GlassStyles.hero`) over a scrim; solid fallback color supplied for reduced transparency |
| **Standard card / tile** | Default **solid** `paper-raised`, `1.dp` hairline, `24.dp` (card) / `20.dp` (nested tile) radius. Promote to glass only for a focal side-column panel (§2.2 budget) |
| **Primary button** | Solid lime pill (never glass), `ink` text, `48.dp` tall, `signal-stroke` in light mode, no shadow; press darkens the fill ~12% |
| **Secondary button** | Same shape; `sky` (go/resume) or tonal `paper-raised` (neutral/enroll). Success CTA in lesson panel is `mint`. A neutral secondary sitting on a glass surface may itself be glass (`GlassStyles.control`) |
| **Filter chip / tab** | Pill; inactive = tonal fill + hairline, **active filter = solid `onSurface` fill + `surface` text**, **active tab/nav = lime** |
| **Segmented control** | Pill container; selected segment gets its own tint (difficulty segments follow difficulty tints) |
| **Progress bar** | `8.dp` pill track on `paper-sunken`, no border; `mint` fill (completion). Segmented variant for chapters; lime for the in-progress segment |
| **Donut gauge** | Thick ring on `paper-sunken`, tri-color arc for difficulty split, big numeral center |
| **Mastery chip** | Skill name + band color + numeric mastery in mono + band label; opaque band-tinted fill with a hairline, never glass. *Unchanged semantics — still the canonical model-output component* |
| **Skill node (Learn)** | See §4.2 node table; solid fill (band color when completed) with a `2.dp` `surface` ring to separate it from the path, no shadow; locked = desaturated + lock glyph |
| **Difficulty pill** | Text label always (`Easy` / `Medium` / `Hard`), tinted fill, mono |
| **Stat tile** | Mono caption with icon, display numeral, small mono footnote; solid tonal tile with hairline |
| **Checklist row** | Checkbox, title, subtitle, right-aligned `+XP`; done = struck-through + mint check |
| **Weekly-rhythm tile** | `20.dp` tile with weekday label and date; done / today / locked (hairline dashed) |
| **League row** | Rank medallion, avatar, name (+flag), points · tier, optional delta; **self row = lime** |
| **Heatmap** | Rounded-square cells, 5 intensity steps, month header, weekday axis, legend |
| **Course card** | Variants: *featured* (tinted header + colored CTA), *catalog* (tag row, pill, footer CTA), *in-progress* (adds bar + Resume), *community* (author + rating + syllabus link) |
| **Table row** | Square rows inside a rounded (`24.dp`) **solid** container, hairline dividers, status icon, pills for duration/difficulty, stacked tag chips, icon-button action. Never glass (dense, lazy) |
| **Badge card** | Colored circular glyph, name, mono requirement; locked badges grey out |
| **Tooltip call-out** | Dark glass pill with lime bolt glyph and light text (solid `ink` if the glass budget is exceeded) — used only for the current node |
| **Question card** | Raised **solid** card, `24.dp` radius, hairline; typeset math **[no mockup]**. Solid so math legibility never depends on the backdrop |
| **Explanation panel** | Expands under feedback state; includes interactive worked steps **[no mockup]** |
| **Recommendation card** | Next-Milestone treatment + `reason` string + start button |
| **Progress trend chart** | Line/area per skill over time, mastery-band line color |
| **Empty / error states** | Specific, contextual guidance in solid tonal containers |

## 6. Surface System & Compose Styles API (`@styles` skill)

Surface styling has two seams: **Haze** for the mesh backdrop and glass, and the
Compose **Styles API** (`androidx.compose.foundation.style.Style`) for
everything that is not glass. Shapes come from one object, `AppShapes`.

### 6.1 Prerequisites & dependencies

- Styles API prerequisites unchanged: `compileSdk` 37+,
  `androidx.compose.foundation:foundation` `1.12.0-alpha01`+, and the opt-in:
```kotlin
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=androidx.compose.foundation.style.ExperimentalFoundationStyleApi")
    }
}
```
- **Haze 2.1.0** (released 2 Nov 2026). Use the same version for every Haze
  artifact. Glass is stable in 2.1, so no `@ExperimentalHazeApi` opt-in. Haze
  2.1.0 is built against Kotlin 2.4.21 and Compose Multiplatform 1.12.2; keep
  the client on compatible versions.
- **ProShape** (`com.riadmahi:proshape`) for continuous corners.
- **Mesh gradient** — the app's own library.

```kotlin
commonMain.dependencies {
    implementation("dev.chrisbanes.haze:haze:2.1.0")
    implementation("dev.chrisbanes.haze:haze-glass:2.1.0")
    implementation("dev.chrisbanes.haze:haze-glass-material3:2.1.0") // optional: surface color from the theme
    implementation("com.riadmahi:proshape:<latest-version>")
    // implementation("<your mesh-gradient library>")
}
```

### 6.2 Theme root

`materialKolor` setup is unchanged (§2.1, `primary`/`onPrimary` still pinned to
`signal`/`ink`). The theme now also owns the Haze state, the glass defaults and
the backdrop.

```kotlin
val LocalHazeState = staticCompositionLocalOf<HazeState> { error("No HazeState") }

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
    val hazeState = rememberHazeState()

    DynamicMaterialExpressiveTheme(
        state = dynamicThemeState,
        motionScheme = MotionScheme.expressive(),
        animate = true,
    ) {
        // Composable; follows theme changes. Keep it here, not per call site.
        val glassBase = GlassStyle.material3()

        CompositionLocalProvider(
            LocalHazeState provides hazeState,
            LocalGlassStyle provides glassBase,
            LocalGlassAccessibilitySettings provides rememberGlassAccessibility(),
            // + AccentTokens / MasteryColors as before
        ) {
            Box(Modifier.fillMaxSize()) {
                AppBackdrop(hazeState)      // the ONLY hazeSource
                content()
            }
        }
    }
}
```

`rememberGlassAccessibility()` is an `expect`/`actual` that maps the platform's
reduce-transparency, increase-contrast and bold-border settings onto
`GlassAccessibilitySettings(reduceTransparency, increaseContrast, showBorders)`,
because Haze does not read platform accessibility settings itself. Haze also
picks the light or dark glass response from the host's system appearance
(`LocalConfiguration.uiMode` on Android, `LocalSystemTheme` elsewhere), **not**
from `MaterialTheme`. The in-app light/dark toggle must therefore be propagated
to that host value for the subtree, otherwise glass stays in the wrong mode
(§6.7, risk 3).

### 6.3 Backdrop

```kotlin
@Composable
fun AppBackdrop(hazeState: HazeState, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val accents = LocalAccentTokens.current
    val colors = remember(scheme, accents) { meshPalette(scheme, accents) } // 3x3, row-major

    // TODO: replace with the mesh-gradient composable from the app's own library.
    // Keep `.hazeSource(hazeState)` on it, and keep it the only source.
    YourMeshGradient(
        colors = colors,
        modifier = modifier.fillMaxSize().hazeSource(hazeState),
    )
}

/** Starting palette. Low chroma, narrow tonal band (§2.2). Tune with the contrast test. */
fun meshPalette(s: ColorScheme, a: AccentTokens): List<Color> = listOf(
    s.background,           s.primaryContainer,        lerp(s.background, a.signalSoft, 0.35f), // one glow point
    s.secondaryContainer,   s.surfaceContainer,        s.tertiaryContainer,
    s.background,           s.surfaceContainerHigh,    s.primaryContainer,
)
```

### 6.4 Shapes

```kotlin
object Radii {
    val hero = 32.dp
    val card = 24.dp
    val tile = 20.dp
    val button = 24.dp   // 48.dp tall: half height
    val chip = 14.dp     // 28.dp tall: half height
}

object AppShapes {
    private fun squircle(r: Dp) =
        ProSquircleShape(topStart = r, topEnd = r, bottomEnd = r, bottomStart = r)

    val hero = squircle(Radii.hero)
    val card = squircle(Radii.card)
    val tile = squircle(Radii.tile)
    val button = squircle(Radii.button)
    val chip = squircle(Radii.chip)
}
```

### 6.5 Glass surfaces

`GlassStyle`s are immutable and compare by identity in Haze 2.1, so they are
**hoisted, never built inside composition**. Each surface applies
`LocalGlassStyle` (the Material 3 base from §6.2) first and then its own style,
so these only set what differs.

```kotlin
object GlassStyles {
    val hero = GlassStyle { shape(RoundedCornerShape(Radii.hero)) }
    val card = GlassStyle { shape(RoundedCornerShape(Radii.card)) }
    val bar = GlassStyle { shape(RoundedCornerShape(Radii.card)) }
    val control = GlassStyle {
        shape(RoundedCornerShape(Radii.button))
        pressed {
            lightingIntensity(1f)
            scale(0.98f)
        }
    }
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    style: GlassStyle = GlassStyles.card,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.hazeGlass(
            input = HazeInput.Backdrop(LocalHazeState.current),
            style = style,
        ),
        content = content,
    )
}
```

For tappable glass, share one `MutableInteractionSource` between the click
behavior and `hazeGlass(..., interactionSource = ...)`, and pass
`interactionReducedMotionPolicy = GlassReducedMotionPolicy.System` so
reduce-motion is respected. Wrap the top-bar chips and segmented controls in
`Modifier.hazeGlassContainer()`. Leave `HazeFeatureFlags.isPlatformBackdropEnabled`
off: the native Android backdrop path is experimental.

### 6.6 `ComponentStyles.kt` — solid surfaces

Solid surfaces keep using `Style`. Everything resolves from the dynamic scheme
and the shape tiers above. No shadows.

```kotlin
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.unit.dp

object ComponentStyles {

    /** Standard solid card — dense or repeated content */
    val cardStyle: Style = Style {
        background(colors.surfaceContainer)                       // paper-raised
        shape(AppShapes.card)
        border(width = 1.dp, color = colors.onBackground.copy(alpha = 0.10f))
        padding(16.dp)
    }

    /** Nested tile inside a card or glass surface */
    val tileStyle: Style = Style {
        background(colors.surfaceContainer)
        shape(AppShapes.tile)
        border(width = 1.dp, color = colors.onBackground.copy(alpha = 0.10f))
        padding(12.dp)
    }

    /** Pill chip / tag — tint supplied by caller via `style` override */
    val chipStyle: Style = Style {
        shape(AppShapes.chip)
        padding(horizontal = 12.dp, vertical = 4.dp)
        textStyle(typography.labelSmall) // mono, >= 11sp
    }

    /** Primary lime button */
    val primaryButtonStyle: Style = Style {
        background(colors.primary)            // signal (lime) — pinned override, see §2.1
        shape(AppShapes.button)
        border(width = 1.dp, color = AccentTokens.signalStroke) // transparent in dark mode
        minWidth(120.dp)
        minHeight(48.dp)
        textStyle(typography.labelLarge)

        pressed {
            background(colors.primary.copy(alpha = 0.88f))
        }
        disabled {
            background(colors.surfaceVariant)
        }
    }

    /** Skill mastery chip — opaque band-tinted fill supplied by caller; never glass */
    val masteryChipStyle: Style = Style {
        shape(AppShapes.chip)
        border(width = 1.dp, color = colors.onBackground.copy(alpha = 0.10f))
        padding(horizontal = 10.dp, vertical = 4.dp)
    }
}

// Theme extensions on StyleScope for token resolution
val StyleScope.colors: ColorScheme
    @Composable get() = MaterialTheme.colorScheme

val StyleScope.typography: Typography
    @Composable get() = MaterialTheme.typography
```

Accent tokens (`flame`, `sky`, `grape`, `rose`, `mint`, `signal-soft`,
`signal-stroke`) and mastery-band colors are **not** Material roles; read them
from the app-level `AccentTokens` / `MasteryColors` CompositionLocals inside
`Style { }` blocks (the `AccentTokens.signalStroke` above stands for that read;
adapt it to however the locals are exposed in `StyleScope`).

Custom components accept a `style: Style = Style` parameter and apply
`Modifier.styleable()`:

```kotlin
@Composable
fun AppButton(
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

### 6.7 Known risks — verify before freezing tokens

1. **Glass corners vs ProShape corners.** Per the Haze docs, `GlassStyle`'s
   `shape` takes a `RoundedCornerShape`, so glass boundaries use circular-arc
   corners while solid surfaces use ProShape squircles. Side by side, a glass
   hero and a solid card can show a subtle corner mismatch. Options: accept it
   for v1; or add an outer `clip(AppShapes.x)` on the glass surface, which also
   clips child content to the squircle but can trim the rim highlight at the
   corners. Judge on a device before choosing.
2. **ProShape fitness.** Its last release is 1.0.0, over a year old. Its README
   describes iOS-style squircles and lists Android and iOS, with a desktop note;
   it does not state G2 (curvature) continuity. Confirm it builds against the
   client's current Kotlin / Compose Multiplatform versions and every target you
   ship, and check the corners visually. If true G2 is a requirement, the
   Capsule family of libraries states G2 explicitly (D7).
3. **In-app theme toggle vs Haze appearance** (see §6.2).
4. **Glass cost.** Measure scrolling screens on a low-end device. Start with
   `HazePerformanceMode.Default`; Haze's own docs warn that glass containers
   can be expensive in dense scrolling layouts.
5. **Mesh library API.** §6.3 uses a placeholder composable. Whatever replaces
   it must expose a drawn layer that can carry `Modifier.hazeSource`.
6. **Lime on light.** `signal-stroke` at ~50% is a starting value; check the
   resulting boundary contrast on the real mesh.

## 7. Motion

One deliberate moment per major action, plus physical press mechanics:

- **Press** — glass controls use Haze's press response (`scale(0.98f)` + lighting, material and content); solid controls darken their fill ~12%. Reduce-motion snaps instead of animating (`GlassReducedMotionPolicy.System`).
- **Mesh drift (optional)** — a very slow control-point drift, ≥ 20s per cycle, off by default on Compact and always off under reduce-motion or battery saver.
- **Streak ring / goal chip fill** on session completion.
- **Mastery-band transition** — brief color animation on a node, chip or Topic Mastery bar when a skill crosses a band threshold.
- **Current node** — slow, subtle ring pulse and tooltip bob (≤ 2s loop, disabled under reduce-motion).
- **Skill-tree path** — completed stretch draws in when a node is newly solved.
- **Correct / incorrect answer** — quick state change on the question card itself.
- **Unit/lesson progress segments** — fill left-to-right once on entering the screen.

## 8. Accessibility

- Mastery bands carry explicit labels (`Needs Attention`, `Developing`, …) alongside colors; difficulty is always a text pill; node state (solved / current / locked / boss) is exposed in the accessibility label, not by color or glyph alone.
- **Lime is a fill, never a text color** — text on lime is always `ink`. Light-grey/pastel-on-pastel mono captions must meet 4.5:1; floor of 11sp.
- Minimum touch target 44×44dp on table rows' action buttons, skill nodes, filter chips, heatmap cells (cells may be smaller visually but tappable area expands or the heatmap offers a tap-to-inspect tooltip).
- **Glass legibility:** `onSurface` text on glass passes 4.5:1 at the worst point of the mesh (tested, §2.2). Text-bearing glass uses `regular`, never `clear`.
- **Reduced transparency / increased contrast / visible borders** are mapped to Haze's `GlassAccessibilitySettings` (§6.2). With reduced transparency, glass falls back to a more diffused, more opaque material; supply an opaque fallback color that matches the in-app theme.
- Solid surfaces carry a `1.dp` hairline; lime fills carry `signal-stroke` in light mode so every control keeps a ≥ 3:1 boundary. Dark-mode equivalents per §2.1.
- No meaning depends on refraction or blur, which fallback renderers omit.
- Timer on Question screen is informational and never auto-submits.
- Reduce-motion setting disables pulse/bob and path-draw animations.

## 9. What to actively avoid

- No drop shadows of any kind (hard-offset or gaussian), and no thick (≥ 2.dp) ink outlines on cards or buttons: those belong to the retired neobrutalist language. Depth comes from glass, tone and hairlines.
- No glass-on-glass nesting, no glass on table rows or other lazy-list items, and no semantic color (mastery, difficulty, accent tags) rendered as glass over the mesh.
- No saturated or high-contrast mesh: the mesh is atmosphere, not content.
- No infinite streak-guilt mechanics: no streak-freeze/insurance **purchases**, no loss-framed copy.
- **Boosters and timers are informational, not pressure.** `2X BOOST`, `XP happy hour`, `Streak Saver`, and reset countdowns may be shown but must not be monetised, must not gate learning, and must not appear in notifications as loss warnings.
- **Hearts (`3 HEARTS LEFT`) must never lock a learner out of practice.** If kept at all, they only affect XP on a lesson attempt and refill without payment (see §10 decision D1).
- No generic SaaS-dashboard card grid on Home — hero + timeline + side rail is the composition.
- Lime is the action color: don't use it for decoration beyond hero glows/highlights, and never use it (or any mint/accent) to encode mastery — mastery bands stay distinct and semantic (§2.1 separation rule).
- No raw LaTeX on screen.

## 10. Mockup issues to resolve (not to copy) & open decisions

**Mockup issues**

1. **Raw LaTeX is compiled and redered** (`$$P(E) = 1 - …$$`, `$f(g(x))$`) in the daily problem, concept panel and lesson description. Math must be typeset; pick a KMP-compatible renderer and treat it as a shared component used by Question, Explanation, and lesson content.
2. **Practice table title column is crushed** at the shown width (titles wrap 4–5 lines). Set column minimums, truncate tags, and use row cards on compact (§4.4).
3. **Text overlap/clipping:** `SOLVED 142 / 420` overlaps the donut caption; the heatmap clips at the right edge; the Weekly League "You" row wraps awkwardly; the Learn tree's start tooltip overlaps the node label. Reserve space for these.
4. **Topic Mastery and difficulty colors collide with mastery bands** — resolved by the separation rule in §2.1.
5. **Data consistency:** Profile shows `142 / 420 problems` and the sidebar shows `142 / 198 quests` — define *quest* vs *problem* once and use consistent terms; the user's league points differ between Home (3,450) and Profile (2,840) — decide whether these are weekly XP, total points, or different leagues and label accordingly. The Learn tree is titled "Linear Calculus Tree" under a *Calculus & Infinite Series* unit.
6. **Tiny labels** (~9–10sp mono) throughout — raise to the 11sp floor.
7. **Surface styling is superseded.** All five mockups use thick ink borders, hard offset shadows and flat paper. Keep their layout, content and hierarchy; apply §2.2 for surfaces.

**Open decisions**

- **D1 — Hearts / Streak Saver / 2× boost / happy hour.** These come from the mockups but sit in tension with the "no guilt mechanics" principle. Recommended: keep boosts and savers as earned, free, informational; drop hearts or make them XP-only.
- **D2 — Elo rating vs. mastery.** Global Elo (a competitive rating from duels/problems) is a different quantity from per-skill DKT mastery. Keep them visually separate (Elo = grape, mastery = band colors) and never show Elo as "skill level".
- **D3 — Subject scope.** The mockups are math-specific (AXIOM, theorem/proof vocabulary). Confirm whether AXIOM is the whole product or the first subject pack; if the latter, keep nav tag-pills, titles ("Euler"), and copy data-driven rather than hard-coded.
- **D4 — Dark mode and compact layouts** are derived, not mocked. Schedule a mockup pass before implementation.
- **D5 — Restyled mockups.** Re-skin at least Home, Learn and Practice in the new language (mesh + glass + continuous corners) before building, so the glass/solid split and the type adjustments in §2.3 are judged on real screens.
- **D6 — Glass budget and device floor.** Decide the lowest supported device tier and measure glass cost on it; the "~6 glass surfaces" guideline in §2.2 is a starting point, not a measurement.
- **D7 — Corner library.** Confirm ProShape holds up (§6.7, risks 1–2). If G2 fidelity matters more than the current library choice, swap `AppShapes` to a library that states G2 continuity; no other file changes.