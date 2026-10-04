# Design — KMP Client

Companion to `AGENTS.md` / `ARCHITECTURE.md`. This is the visual and
interaction spec for the KMP + Compose Multiplatform client. It should read
as opinionated and specific to this product — a knowledge-tracing platform
where a real model tracks per-skill mastery — not a generic ed-tech shell.

> **Source of truth:** the five AXIOM mockups (Home, Learn, Discover,
> Practice, Profile). Where this document and a mockup disagree, the mockup
> wins *unless* the item is listed in §10 (mockup issues we are deliberately
> not copying). Screens with no mockup yet (Question, Answer feedback, Auth,
> Onboarding, dark mode) are marked **[no mockup]** and must be restyled to
> match this language when designed.

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

Warm paper, hard ink, and one loud **volt-lime** action color. Lime is the
brand/action signal; **green is no longer the primary** — it now means
"success / progress / easy". The mastery scale is functional (it encodes
model output), not decorative, and is strictly tied to DKT thresholds.
Hex values below were sampled from the mockups — confirm against the design
source before freezing tokens.

| Token | Hex | Role |
|---|---|---|
| `ink` | `#14172B` | Text, all borders, hard shadows on light surfaces |
| `paper` | `#FBFAF6` | Page background |
| `paper-raised` | `#F6F4EE` | Cards, sheets, nested containers (warm off-white, not pure white) |
| `paper-sunken` | `#E9E6DD` | Inset tracks, empty progress bars, disabled/locked fills, table header |
| `signal` | `#E4FF1A` | **Primary action/brand (volt-lime)** — primary CTAs, active nav item, "You" rows, current node, selected chips. Always carries `ink` text, never used as text color |
| `signal-soft` | `#F4FFA0` | Hero-card glow, subtle highlight panels, "NEXT" lesson row |
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

**Dark mode ("Deep Work") [no mockup — proposal]:** background `#0F1220`,
cards `#171B2E`, text `#EAEBF2`. `signal` stays lime (it pops on dark) with
`ink` text. Borders become light (`#EAEBF2`, 2.dp) so the outline language
survives; hard shadows use `#05070F` on cards and `signal` on the single
primary CTA of a screen. Accent tints darken ~30%. Needs a mockup pass
before build.

### 2.2 Neobrutalist shape & shadow language

Bold, high-contrast, hard-edged — but with **very large corner radii**.
The mockups are neobrutalist in border/shadow and soft-geometric in shape:

- **Hard offset shadows only**, zero blur. Three tiers, always bottom-right:
    - Hero/section card: `3.dp` ink border, `8.dp` x/y shadow
    - Standard card, tile, nav item: `2.dp` border, `4.dp` shadow
    - Chip, pill, inline tag: `1.5.dp` border (or none for tinted tags), `2.dp` shadow max
- **Corner radii:** section/hero cards `28–32.dp`; nested cards and tiles `20–24.dp`; buttons, chips, tags, search, stat pills are **full pills**. Table containers `24.dp` with square internal rows.
- **Tactile press:** buttons, nav items, and tappable cards translate toward the shadow (`4.dp → 1.dp`) in one frame and return on release.
- **Selected state = lime fill + ink border + shadow** (active nav item, active tab, active filter chip uses solid `ink` fill with paper text instead — see §5).
- **Decoration:** large cards may carry a single oversized, low-contrast glyph or radial `signal-soft` glow in a corner (e.g. the faint *fx* on the daily-problem card, lime glow on hero cards). One per card, never behind text that needs to be read.
- **Compose Styles API:** all of the above is expressed through `androidx.compose.foundation.style.Style` (§6).

### 2.3 Type

The mockups moved away from a serif display face to a tight, heavy grotesque
plus a heavy use of mono for labels.

- **Display / headline — a tight, bold grotesque** (Space Grotesk or Bricolage Grotesque; pick one and verify against the mockup). Page titles, hero titles, card titles, big numerals (142, 2,140, 18). Tight tracking (−2% to −3%) at display sizes. *Replaces Fraunces.*
- **UI / body — IBM Plex Sans** (or the grotesque's text cut). Descriptions, nav labels, buttons, question text.
- **Data / labels — mono** (IBM Plex Mono or JetBrains Mono). Eyebrows (`UNIT 03 · PATH TO MASTERY`), tags, stat captions, timers, XP, percentages, table headers, nav tag-pills. Eyebrows and table headers are **UPPERCASE with +6–8% tracking**.
- Scale: 1.25 ratio from a 15sp base. **Floor of 11sp** for any mono label (several mockup labels are ~9–10sp and fail legibility). Body line length caps at ~70 characters.
- **Math is typeset, not printed.** Formulas such as `P(E) = 1 − 365!/…` and `f(g(x))` must render as real math (see §10); raw `$…$` / `$$…$$` is never shown to a student.

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
- Sidebar and top bar are separated from content by a **4.dp solid ink rule** (right edge of sidebar, bottom edge of top bar). Both stay fixed while content scrolls.

## 3. Navigation

Five items, bottom bar (compact) / sidebar (expanded). Each item has an icon,
a label, and — in the expanded sidebar — a **right-aligned mono tag-pill**
naming what lives inside. The active item is a lime fill with ink border and
hard shadow.

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
should be built with the card/chip/shadow language above.

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
- **Community Learning Tracks & Micro-Courses** — bordered section with `Propose a Track` button; 3-up cards with a colored category tag (`Olympiad` rose, `Foundations` grape, `Micro-Track` sky), duration, title, description, author line (name + affiliation), rating, `VIEW SYLLABUS →`.

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
| **Top-bar chip** | Pill, `2.dp` ink border, `2.dp` shadow, icon (colored) + two-line mono value (`18` / `Days`). Streak = flame, XP = grape gem, Goal = mint ring |
| **Nav item** | Pill-ish row: icon, label, right-aligned tag-pill. Active = lime fill, `2.dp` border, `4.dp` shadow |
| **Eyebrow chip** | Small pill, UPPERCASE mono, tinted background (lime for context, sky/orange/grape by meaning) |
| **Section / hero card** | `3.dp` border, `28–32.dp` radius, `8.dp` hard shadow, `paper-raised`; optional corner glow/glyph |
| **Standard card / tile** | `2.dp` border, `20–24.dp` radius, `4.dp` shadow |
| **Primary button** | Lime pill, ink text (uppercase label for hero CTAs), `2.dp` border, `4.dp` shadow, presses to `1.dp` |
| **Secondary button** | Same shape; `sky` (go/resume) or `paper-raised` (neutral/enroll). Success CTA in lesson panel is `mint` |
| **Filter chip / tab** | Pill; inactive = bordered paper, **active filter = solid ink fill + paper text**, **active tab/nav = lime** |
| **Segmented control** | Pill container; selected segment gets its own tint (difficulty segments follow difficulty tints) |
| **Progress bar** | `12.dp` pill track on `paper-sunken` with `2.dp` ink border; `mint` fill (completion). Segmented variant for chapters; lime for the in-progress segment |
| **Donut gauge** | Thick ring on `paper-sunken`, tri-color arc for difficulty split, big numeral center |
| **Mastery chip** | Skill name + band color + numeric mastery in mono + band label; `2.dp` border, `2.dp` shadow. *Unchanged — still the canonical model-output component* |
| **Skill node (Learn)** | See §4.2 node table; `3.dp` ink outline, `4.dp` hard shadow; locked = desaturated + lock glyph |
| **Difficulty pill** | Text label always (`Easy` / `Medium` / `Hard`), tinted fill, mono |
| **Stat tile** | Mono caption with icon, display numeral, small mono footnote; border + `4.dp` shadow |
| **Checklist row** | Checkbox, title, subtitle, right-aligned `+XP`; done = struck-through + mint check |
| **Weekly-rhythm tile** | Square `2.dp` tile with weekday label and date; done / today / locked(dashed) |
| **League row** | Rank medallion, avatar, name (+flag), points · tier, optional delta; **self row = lime** |
| **Heatmap** | Rounded-square cells, 5 intensity steps, month header, weekday axis, legend |
| **Course card** | Variants: *featured* (tinted header + colored CTA), *catalog* (tag row, pill, footer CTA), *in-progress* (adds bar + Resume), *community* (author + rating + syllabus link) |
| **Table row** | Square rows inside a rounded bordered container, hairline dividers, status icon, pills for duration/difficulty, stacked tag chips, icon-button action |
| **Badge card** | Colored circular glyph, name, mono requirement; locked badges grey out |
| **Tooltip call-out** | Black pill with lime bolt glyph, paper text — used only for the current node |
| **Question card** | Raised card with `2.dp` ink border and `4.dp` hard shadow; typeset math **[no mockup]** |
| **Explanation panel** | Expands under feedback state; includes interactive worked steps **[no mockup]** |
| **Recommendation card** | Next-Milestone treatment + `reason` string + start button |
| **Progress trend chart** | Line/area per skill over time, mastery-band line color |
| **Empty / error states** | Specific, contextual guidance in bordered containers |

## 6. Component Styles Architecture & Jetpack Compose Styles API (`@styles` skill)

All UI components integrate the **Jetpack Compose Styles API**
(`androidx.compose.foundation.style.Style`), defining unified Neobrutalist
styling tokens and state-driven behavior in `ComponentStyles.kt`.

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

Three card tiers (hero / standard / chip) plus button and mastery-chip
styles. Radii and offsets are named constants so tiers stay consistent.

```kotlin
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.unit.dp

object ComponentStyles {

    /** Hero / section card — biggest radius, deepest shadow */
    val heroCardStyle: Style = Style {
        background(colors.surface)            // paper-raised
        shape(RoundedCornerShape(28.dp))
        border(width = 3.dp, color = colors.onBackground)
        shadow(elevation = 0.dp, offsetX = 8.dp, offsetY = 8.dp, color = colors.onBackground)
        padding(24.dp)
    }

    /** Standard card, tile, nav item */
    val cardStyle: Style = Style {
        background(colors.surface)
        shape(RoundedCornerShape(20.dp))
        border(width = 2.dp, color = colors.onBackground)
        shadow(elevation = 0.dp, offsetX = 4.dp, offsetY = 4.dp, color = colors.onBackground)
        padding(16.dp)
    }

    /** Pill chip / tag — tint supplied by caller via `style` override */
    val chipStyle: Style = Style {
        shape(CircleShape)
        border(width = 1.5.dp, color = colors.onBackground)
        shadow(elevation = 0.dp, offsetX = 2.dp, offsetY = 2.dp, color = colors.onBackground)
        padding(horizontal = 12.dp, vertical = 4.dp)
        textStyle(typography.labelSmall) // mono, uppercase, tracked, >= 11sp
    }

    /** Primary lime button with tactile press */
    val primaryButtonStyle: Style = Style {
        background(colors.primary)            // signal (lime) — pinned override, see §2.1
        shape(CircleShape)
        border(width = 2.dp, color = colors.onBackground)
        shadow(elevation = 0.dp, offsetX = 4.dp, offsetY = 4.dp, color = colors.onBackground)
        minWidth(120.dp)
        minHeight(48.dp)
        textStyle(typography.labelLarge)

        pressed {
            shadow(elevation = 0.dp, offsetX = 1.dp, offsetY = 1.dp, color = colors.onBackground)
        }
        disabled {
            background(colors.surfaceVariant)
            shadow(elevation = 0.dp, offsetX = 0.dp, offsetY = 0.dp, color = colors.onBackground)
        }
    }

    /** Skill mastery chip — unchanged semantics, now pill-shaped */
    val masteryChipStyle: Style = Style {
        shape(CircleShape)
        border(width = 1.5.dp, color = colors.onBackground)
        shadow(elevation = 0.dp, offsetX = 2.dp, offsetY = 2.dp, color = colors.onBackground)
        padding(horizontal = 10.dp, vertical = 4.dp)
    }
}

// Theme extensions on StyleScope for token resolution
val StyleScope.colors: ColorScheme
    @Composable get() = MaterialTheme.colorScheme

val StyleScope.typography: Typography
    @Composable get() = MaterialTheme.typography
```

Accent tokens (`flame`, `sky`, `grape`, `rose`, `mint`, `signal-soft`) and
mastery-band colors are **not** Material roles; read them from the app-level
`AccentTokens` / `MasteryColors` CompositionLocals inside `Style { }` blocks.

### 6.3 Applying Styles to Components

Custom components accept a `style: Style = Style` parameter and apply
`Modifier.styleable()`:

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

One deliberate moment per major action, plus physical press mechanics:

- **Button / card / nav press** — 1-frame translate toward the shadow (`4.dp → 1.dp`), return on release.
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
- High contrast from `2–3.dp` solid ink borders on all cards and interactive controls in light mode; dark-mode equivalents per §2.1.
- Timer on Question screen is informational and never auto-submits.
- Reduce-motion setting disables pulse/bob and path-draw animations.

## 9. What to actively avoid

- No soft gaussian drop shadows (`Modifier.shadow(elevation = 8.dp)`) — hard offset shadows only.
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

**Open decisions**

- **D1 — Hearts / Streak Saver / 2× boost / happy hour.** These come from the mockups but sit in tension with the "no guilt mechanics" principle. Recommended: keep boosts and savers as earned, free, informational; drop hearts or make them XP-only.
- **D2 — Elo rating vs. mastery.** Global Elo (a competitive rating from duels/problems) is a different quantity from per-skill DKT mastery. Keep them visually separate (Elo = grape, mastery = band colors) and never show Elo as "skill level".
- **D3 — Subject scope.** The mockups are math-specific (AXIOM, theorem/proof vocabulary). Confirm whether AXIOM is the whole product or the first subject pack; if the latter, keep nav tag-pills, titles ("Euler"), and copy data-driven rather than hard-coded.
- **D4 — Dark mode and compact layouts** are derived, not mocked. Schedule a mockup pass before implementation.
