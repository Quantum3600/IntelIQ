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

Base palette — cool, ink-and-paper, not the cream/terracotta or
near-black/neon combinations that read as generic AI defaults. The mastery
scale is functional (it encodes model output), not decorative, and is the
one place saturated color is earned by content.

| Token | Hex | Role |
|---|---|---|
| `ink` | `#171B2E` | Primary text, icons on light surfaces |
| `paper` | `#EEF0F5` | Default light-mode background (cool, not cream) |
| `paper-raised` | `#FFFFFF` | Cards, sheets, elevated surfaces |
| `signal` | `#4C5FD5` | Primary brand/action color — links, primary buttons, active nav |
| `signal-dim` | `#DDE1F7` | Selected-state backgrounds, subtle highlights |
| `flame` | `#FF6B4A` | Streak, XP, daily-goal — the one warm accent, reserved for momentum/urgency states only |

Mastery scale (matches the product's own mastery bands — see
`API_CONTRACT.md` §4 — so the color a student sees on a skill chip is the
same threshold the backend uses, not a separate design decision):

| Band | Range | Hex | Name |
|---|---|---|---|
| Needs Attention | 0.00–0.30 | `#D95550` | Clay |
| Developing | 0.30–0.50 | `#E1943D` | Amber |
| Intermediate | 0.50–0.70 | `#D9B23C` | Ochre |
| Strong | 0.70–0.85 | `#4C9F70` | Moss |
| Mastered | 0.85–1.00 | `#1F6F54` | Pine |

Never use the mastery scale for anything except mastery. Don't reach for
"amber" as a generic warning color elsewhere — that overloads the one
channel that's supposed to mean one thing.

Dark mode ("Deep Work," for evening study sessions — a real, requested
pattern in study apps, not a token palette-swap exercise): `ink` and `paper`
invert to `#0F1220` background / `#EAEBF2` text; `signal` lightens to
`#7C8AEF` for contrast; the mastery scale and `flame` hold their hue but
gain 8–10% lightness so they don't desaturate against the dark ground.

### 2.2 Type

- **Display/headline — Fraunces** (variable serif). Used for the streak/XP
  numerals on the home screen, section headers on the skill map, and score
  reveals after a quiz. It's the one place the product gets to feel
  editorial and confident rather than purely utilitarian — earned by being
  used sparingly, at a handful of moments that deserve weight.
- **UI/body — IBM Plex Sans.** Everything else: nav labels, question text,
  buttons, body copy. Chosen for its technical, slightly engineered
  character — it doesn't read as generic "app font" the way an unmodified
  Inter/Poppins pairing does, and it's a real family (not a one-off
  display face) so it holds up at small sizes in dense screens like the
  practice list.
- **Data/mono — IBM Plex Mono.** Reserved for things that are literally
  data: difficulty scores, timers, streak-day counts, mastery percentages,
  skill IDs. This is a content decision, not decoration — these are numbers
  a student compares run to run, and monospace figures make them easy to
  scan and compare at a glance (an XP counter ticking up reads better in
  tabular figures).

Line length caps at ~70 characters for question/explanation body text; type
scale follows a 1.25 ratio from a 15px UI base, with Fraunces headlines
breaking that scale upward only at the two or three moments above.

### 2.3 Layout concept

Left-aligned, not centered — this is a study tool used in short, frequent
sessions, and left alignment reads faster on repeat visits than centered
hero-style layouts. Mobile is the primary target (Android/iOS per the KMP
build); desktop reflows the same left-aligned content into a wider column
with the skill map gaining a persistent side panel rather than a modal.

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

Five-item bottom nav (mobile) / left rail (desktop) — mapped 1:1 to the
existing `presentation/` packages (`dashboard`, `courses`, `quiz`,
`progress`, `profile`) so the nav structure and the codebase structure stay
the same shape:

| Nav item | Maps to | Primary job |
|---|---|---|
| **Home** | `dashboard` | Continue-learning card, streak/daily-goal, today's recommendation |
| **Path** | `courses` | Khan-Academy-style mastery map — skills as connected nodes, not a flat list |
| **Practice** | `quiz` | LeetCode-style filterable problem/question arena |
| **Progress** | `progress` | Per-skill mastery, trend charts, attempt history |
| **Profile** | `profile` | Stats, achievements, settings |

Streak flame + XP counter live in a persistent top bar across all five tabs
(Duolingo pattern) — it's state that matters everywhere, not one screen's
content.

## 4. Screen inventory

Extends the flow already defined in `AGENTS.md`
(`Splash → Auth → Home → Course → Topic → Question → Answer → Feedback →
Progress → Recommendation`) with the screens each reference app makes a
strong case for:

1. **Splash** — brief, no animation beyond a single logo-mark fade.
2. **Onboarding / placement check** — a short adaptive diagnostic (Brilliant
   pattern) that gives the KT model a cold-start signal instead of starting
   every student at zero information (ties directly to the cold-start path
   in `AGENTS.md` §4).
3. **Auth** — login/register/forgot-password; minimal, one primary action
   per screen.
4. **Home (Dashboard)** — continue-learning card, streak ring + daily goal,
   "Recommended next" (surfaces `GET /recommendations/next`), recent
   mastery movers ("Linear Equations moved Developing → Intermediate").
5. **Path (Skill Map)** — the Khan Academy/Brilliant-inspired centerpiece:
   skills as nodes connected by prerequisite edges, each node colored by
   its live mastery band. This is the one screen where the visual literally
   is the knowledge graph, not a metaphor for it.
6. **Course / Topic detail** — course overview, topic list within it, entry
   point into Practice filtered to that topic.
7. **Practice (Arena)** — LeetCode-style list: columns for difficulty,
   skill tag(s), and status (new / attempted / mastered); filter and sort
   controls pinned above the list, not buried in a sheet.
8. **Question** — one question, generous whitespace, a visible but
   unobtrusive timer (response time is a real model input — see
   `ML_WORKFLOW.md` — so the timer is functional, not just pressure).
9. **Answer feedback** — immediate correct/incorrect state (Duolingo
   pattern), followed by an explorable explanation panel (Brilliant
   pattern: "why," not just "what"), followed by the mastery delta for the
   affected skill(s) rendered as a small before→after mastery-band chip.
10. **Progress (Analytics)** — per-skill mastery list/chart, attempt
    history, response-time trend — the "receipts" screen for the numbers
    Home only teases.
11. **Recommendation detail** — why this was recommended ("targets a weak
    skill at a suitable difficulty" — matches the `reason` field in
    `API_CONTRACT.md` §1), with a direct start action.
12. **Achievements** — badges for streak milestones, first-mastery-in-topic,
    consistency (Khan Academy energy-points lineage), kept understated —
    a small shelf on Profile, not an interstitial that interrupts practice.
13. **Leaderboard (optional, cohort-scoped)** — weekly, resettable, opt-in;
    Duolingo's league mechanic but scoped to a class/cohort rather than
    global, since this is a course platform with real instructors, not an
    open consumer app.
14. **Profile / Settings** — account, notification preferences, theme
    (Focus/Deep Work), data/privacy (ties to the minimal-collection
    principle in `AGENTS.md` §3).

## 5. Core UI components

| Component | Behavior / notes |
|---|---|
| **Mastery chip** | Skill name + band color + numeric mastery in Plex Mono. The single most reused component — appears on Path nodes, Practice list rows, Progress, and the post-answer delta. |
| **Skill node (Path)** | Circular node, mastery-band fill, connecting edges to prerequisite/unlocked skills; locked nodes render desaturated with a lock glyph, never hidden entirely — students should see what's ahead. |
| **Streak ring** | Circular daily-goal progress in `flame`, numeral in Fraunces at center; fills once per completed session, doesn't reset visually until the day rolls over (no punitive "you lost your streak" full-screen interstitial — a quiet banner is enough). |
| **Question card** | Prompt, answer input (MCQ/numeric/short-answer per question type), difficulty badge (Plex Mono), skill tag chip(s). |
| **Difficulty badge** | Small, three-tier by default (Easy/Medium/Hard) mapped from the underlying continuous `difficulty` float — round for display, keep the float for the model. |
| **Explanation panel** | Expands under the feedback state; supports at least one interactive element (a worked step the student can toggle, a re-try-a-similar-question action) rather than static paragraph text only. |
| **Recommendation card** | Question preview + one-line `reason` string + start action — never just a bare "Next" button with no context. |
| **Progress trend chart** | Line/area chart per skill over time; mastery-band color as the line color so Progress and Path read as the same visual language. |
| **Leaderboard row** | Rank, name/avatar, XP, rank-change indicator; cohort-scoped, never shows students outside the student's own class. |
| **Empty/error states** | Written in the interface's voice, specific about what happened and what to do next — "No attempts yet in this topic — start with Practice" rather than a generic empty illustration. |

## 6. Motion

One deliberate moment per major action, not ambient motion everywhere:

- **Streak ring fill** on session completion — the one celebratory beat per
  day.
- **Mastery-band transition** — when a skill crosses a band threshold
  (e.g. Developing → Intermediate), the mastery chip briefly animates
  color from old band to new band. This is the moment the product should
  feel most alive, because it's the one animation tied directly to a real
  model update, not a UI flourish.
- **Correct/incorrect answer** — a single, quick state change on the
  question card itself (border/icon), no full-screen takeover.
- Everything else (nav transitions, list scrolling, sheet opening) uses
  standard platform motion with no added flourish, and respects
  reduced-motion settings.

## 7. Accessibility

- Mastery bands are never color-only: each carries its label (`Needs
  Attention`, `Developing`, …) as visible or accessible text, not color
  alone — a colorblind student must be able to read the map.
- Minimum touch target 44×44dp throughout the Practice list and Path nodes.
- All interactive states (focus, pressed, disabled) are visible, not
  implied by color shift alone.
- Timer on the Question screen is informational, never auto-submits —
  response time is captured for the model, but a student is never
  penalized in the UI for taking longer.

## 8. What to actively avoid

- No mascot character driving copy or empty states (Duolingo's strongest
  brand asset, but it would fight the "real model, real data" identity
  here).
- No infinite streak-guilt mechanics (streak freezes/insurance upsells) —
  keep the daily goal encouraging, not punitive.
- No dense, code-editor-cold Practice screen with zero warmth — difficulty
  and status should be scannable at a glance, per LeetCode, but the type
  and spacing stay closer to Brilliant's calmer density.
- No generic SaaS-dashboard card grid on Home — Home should read as "pick
  up where you left off," not "here are eight widgets."