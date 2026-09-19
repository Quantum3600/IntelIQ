# AGENTS.md — KMP + Ktor Monorepo

This file steers any AI coding agent (Claude Code, Codex, Cursor, etc.) working in
this repository. Read this before making changes. It is scoped to **this repo
only**: the Kotlin Multiplatform (KMP) client and the Ktor application backend.
The ML model lives in a **separate** repository (`fastapi-ml-repo`) and is
reached only over the network — never import, vendor, or embed it here.

For the wire contract between this repo and the ML service, see
`API_CONTRACT.md` (kept in sync across both repos — treat it as the source of
truth over anything inferred from code).

## 1. What this repo is

Dynamic Knowledge Tracing Education Platform — student-facing client plus the
business-logic backend. Three actors matter for every feature you touch:
Student, Instructor, Administrator, plus the ML/AI service as a non-human
"system actor" this backend calls out to.

Two deployables live in this one repo:

| Deployable | Tech | Responsibility |
|---|---|---|
| `shared` (+ platform targets) | Kotlin Multiplatform, Compose Multiplatform | Android / iOS / Desktop client UI and local state |
| `ktor-backend` | Ktor (Kotlin) | Business logic, auth/authz, durable persistence, orchestration of Redis + MongoDB + calls to FastAPI |

## 2. Repository structure

### 2.1 KMP client (`shared/`)

```
shared/
|-- data/
|   |-- api/
|   |-- repository/
|   +-- local/
|-- domain/
|   |-- model/
|   |-- repository/
|   +-- usecase/
+-- presentation/
    |-- auth/
    |-- dashboard/
    |-- courses/
    |-- quiz/
    |-- progress/
    |-- recommendation/
    +-- profile/
```

- Architecture: **MVI**, **Navigation 3**, Compose Multiplatform for UI.
- Primary screen flow: `Splash -> Login/Signup -> Home (Courses, Continue
  Learning, Recommended, Progress, Profile) -> Course -> Topic -> Question ->
  Answer -> Feedback -> Updated Mastery -> Next Recommendation`. Any new
  screen should slot into this flow rather than branching around it without
  discussion.
- Targets: Android, iOS, Desktop (Web is aspirational per the SRS — confirm
  current target set in `build.gradle.kts` before assuming Web is live).
- `presentation/` packages line up 1:1 with product areas (dashboard, courses,
  quiz, progress, recommendation, profile) — put new screens in the matching
  package rather than creating a new top-level one unless it's a genuinely new
  product area.

### 2.2 Ktor backend

Six functional groupings, each following the same layering:

```
Route -> Controller -> Service -> Repository -> Database (MongoDB)
```

Groupings: **Auth, Users, Courses, QnA** (questions + attempts), **Progress**,
**Recommendations**. When adding an endpoint, place it in the matching
grouping and keep the four-layer shape — don't let a Route call a Repository
directly, and don't put business rules in the Controller layer.

- Ktor talks to **Redis directly** for anything fast/short-lived: sessions,
  rate limiting, cached responses (`student:{id}:kt_state`,
  `student:{id}:mastery`, `student:{id}:recommendations`,
  `student:{id}:session`).
- Ktor writes durable records **straight to MongoDB**.
- Ktor calls **FastAPI** only for the ML inference step of grading an
  attempt — never for anything else, and never synchronously blocking the
  attempt save (see §4).

## 3. Hard architectural constraints

These come directly from the SRS and are non-negotiable without a spec
change — flag it to the user rather than silently working around one:

1. **The Ktor server never contains or executes the ML model.** All
   inference is delegated to FastAPI. If you find yourself importing a
   model, a checkpoint, or PyTorch/ONNX into this repo, stop — that belongs
   in `fastapi-ml-repo`.
2. **FastAPI is not publicly reachable.** This repo is the only public
   entry point (`Internet -> Ktor (public) -- private network --> FastAPI
   (internal only)`). Never add code that exposes a FastAPI route directly
   to the internet or to the KMP client.
3. **Attempts are immutable.** Never update or delete an `Attempt` document.
   Corrections are made by recording a *new* attempt, never by mutating an
   old one.
4. **Model weights are not updated synchronously.** Training is a separate,
   periodic, offline process owned by the ML repo — this repo only ever
   *calls* inference, it never triggers or performs training.
5. **KT prediction and recommendation are distinct components.** Don't
   collapse them into one service/class — they must stay independently
   testable (see §6).
6. **Raw model internals (e.g. GRU hidden state vectors) are never exposed**
   to the client or in any API response. Only derived, student-facing
   values (mastery, probability, explanation text) cross the Ktor -> KMP
   boundary.
7. **Minimal collection principle for privacy:** only store the fields
   knowledge tracing actually needs (student id, question, skill, response,
   correctness, response time, timestamp). Don't add new student behavioral
   fields without a stated purpose.

## 4. Critical data flow: answer submission

This is the flow every change to attempt-handling must preserve:

```
KMP -> POST /api/v1/attempts -> Ktor
Ktor: validate answer -> save attempt to MongoDB (this happens first, always)
Ktor -> FastAPI: { user_id, skill_id, skillIds, difficulty, time_taken_ms, correct }
FastAPI -> preprocessing -> new-user check -> GRU-DKT advances/initializes hidden state
FastAPI -> Ktor: probability of correct next answer + knowledge state
Ktor: cache via Redis as needed -> compute mastery + generate recommendations
Ktor -> KMP: { correct, prediction, skillUpdates, nextRecommendation }
```

Key rule: **the attempt is saved to MongoDB before FastAPI is ever called.**
If FastAPI is unreachable, the attempt must still be persisted and the
knowledge-tracing update retried later (save-then-retry is acceptable for
MVP) — the API must respond gracefully (e.g. "saved, analysis will follow
shortly"), never a generic 500. Any change touching `AttemptService` /
`AttemptRoute` should be checked against this ordering.

The cold-start branch (a student's very first graded attempt) is a
first-class path, not an edge case — a hidden state must exist before any
prediction is attempted for a new student.

## 5. Auth & roles

- JWT access + refresh tokens. Three roles: `STUDENT`, `TEACHER`,
  `ADMIN`(implied by the Administrator user class — confirm exact enum
  naming against `AuthService` before adding a new role).
- **All authorization decisions live in the Ktor layer.** Role checks gate
  every course-management, question-management, and analytics endpoint.
  Never rely on the client to hide UI as the only access control.
- Passwords and emails are hashed; plaintext is never stored, never logged.
- Authentication endpoints must be rate-limited.

## 6. Testing expectations

- **Unit tests (Kotlin/Ktor):** `AuthService`, `QuestionService`,
  `AttemptService`, `RecommendationService`. Any new service class needs a
  matching test class in this list's spirit — don't ship a Service without
  unit coverage.
- **Integration tests:** exercise the full path `KMP -> Ktor -> MongoDB ->
  FastAPI -> Redis`. When you change the attempt-submission flow, add or
  update an integration test that goes through this whole chain (FastAPI
  can be a mock/stub in this repo's test suite — the real model lives in
  the other repo).
- **Security tests:** invalid JWT, expired JWT, wrong role, unauthorized
  question access, unauthorized student-analytics access, direct FastAPI
  access attempts (should fail from outside the private network),
  rate-limit bypass. Any new protected endpoint needs at least the first
  four of these as test cases.

## 7. Non-functional targets to keep in mind

| Requirement | Target |
|---|---|
| Normal API request latency | p95 < 500 ms |
| KT inference call latency (Ktor -> FastAPI round trip) | p95 < 300 ms |

These are engineering targets, not hard research-backed numbers — but a
change that clearly regresses either should be called out in the PR
description.

## 8. Local/dev environment

```
docker-compose
|-- ktor
|-- fastapi        # separate repo/image, referenced here only
|-- mongodb
|-- redis
+-- nginx

Dev path: KMP -> localhost:8080 -> Ktor -> FastAPI:8000
```

Standard commands (confirm against the actual `build.gradle.kts` /
`settings.gradle.kts` once scaffolded — these are the conventional Gradle
Multiplatform equivalents, not a guarantee of exact task names):

```bash
./gradlew build                 # build all targets
./gradlew :ktor-backend:run     # run the backend locally
./gradlew :ktor-backend:test    # backend unit + integration tests
./gradlew :shared:test          # shared KMP module tests
docker-compose up -d mongodb redis   # bring up local dependencies only
```

## 9. Things an agent should never do in this repo

- Add a direct MongoDB/Redis dependency inside `shared/` (the client only
  talks to Ktor's REST API).
- Add code that lets the KMP client call FastAPI directly.
- Silently rename or restructure the six Ktor functional groupings without
  flagging it — other tooling and docs (`API_CONTRACT.md`) assume this
  grouping.
- Weaken role-based access control checks to "make a test pass."
- Introduce a new persisted student-behavior field without checking §3.7.

## 10. Where to look next

- `API_CONTRACT.md` — the exact public and internal API shapes both repos
  must agree on.
- `ARCHITECTURE.md` — deployment topology, data ownership, and consistency
  model in more depth.
