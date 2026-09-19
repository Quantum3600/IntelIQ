# API Contract — Ktor ↔ FastAPI ↔ KMP

This is the shared source of truth for the wire contract across both repos.
Copy this file into the root of **both** `kmp-ktor-repo` and `fastapi-ml-repo`
and keep them identical — if you change a shape on one side, update this file
and the other repo's copy in the same PR/commit. An agent working in either
repo should treat a mismatch between this file and the code as a bug to fix,
not a reason to silently diverge further.

## Network topology

```
Internet -> Ktor (public) -- private network --> FastAPI (internal only)
```

FastAPI must never be reachable from the internet or directly from the KMP
client. Every ML call is proxied through Ktor.

## 1. Public Ktor API (`/api/v1/...`)

| Group | Endpoints |
|---|---|
| Authentication | `POST /api/v1/auth/register`, `/login`, `/refresh`, `/logout` |
| User | `GET /api/v1/users/me`, `PUT /api/v1/users/me`, `GET /api/v1/users/me/statistics` |
| Course | `GET/POST/PUT/DELETE /api/v1/courses[/{courseId}]` |
| Question | `GET/POST/PUT/DELETE /api/v1/questions[/{id}]` |
| Attempt | `POST /api/v1/attempts` |
| Progress | `GET /api/v1/progress`, `/progress/skills`, `/progress/topics`, `/progress/history` |
| Recommendation | `GET /api/v1/recommendations`, `/recommendations/next`, `POST /recommendations/refresh` |

### Example — submit attempt

Request:

```json
POST /api/v1/attempts
{
  "questionId": "q101",
  "answer": "B",
  "responseTimeMs": 18400
}
```

Response:

```json
{
  "attemptId": "attempt101",
  "correct": true,
  "prediction": { "probability": 0.84 },
  "skillUpdates": [
    { "skillId": "linear-equation", "mastery": 0.81, "status": "STRONG" }
  ],
  "nextRecommendation": {
    "questionId": "q102",
    "reason": "Practice developing skill"
  }
}
```

Note what is **not** in this response: no hidden-state vector, no raw model
logits. Only derived, explainable values reach the client.

## 2. Internal FastAPI API

Reachable only from Ktor, over the private network, versioned independently
of the public API.

### 2.1 Predict — `POST /v1/predict`

Request:

```json
{
  "user_id": "user123",
  "skill_id": "002",
  "skillIds": ["linear-equation"],
  "difficulty": 0.6,
  "time_taken_ms": 18000,
  "correct": 1,
  "query_skill_id": "002"
}
```

Response:

```json
{
  "user_id": "user123",
  "query_skill_id": "002",
  "probabilityCorrect": 0.84,
  "knowledgeState": { "linear-equation": 0.81 },
  "confidence": 0.76,
  "modelVersion": "gru-dkt-v1"
}
```

### 2.2 Mastery — `POST /v1/mastery`

Response:

```json
{
  "studentId": "user123",
  "skills": [
    { "skillId": "algebra", "mastery": 0.82 },
    { "skillId": "geometry", "mastery": 0.47 }
  ]
}
```

> Per each repo's `AGENTS.md`: the SRS's mastery-ownership sections (5.3.3)
> say mastery/recommendation are computed by **Ktor**, not FastAPI, from the
> prediction history FastAPI returns — while this endpoint and the FastAPI
> testing section imply mastery logic living in the ML service. Treat this
> endpoint as **either not yet implemented or slated for confirmation with
> the team** rather than assuming which side owns it. Don't build both
> implementations "just in case."

### 2.3 Recommend — `POST /internal/v1/recommend`

Request:

```json
{
  "studentId": "user123",
  "candidateQuestionIds": ["q1", "q2", "q3", "q4"]
}
```

Response:

```json
{
  "recommendations": [
    {
      "questionId": "q3",
      "score": 0.91,
      "reason": "Targets weak skill at suitable difficulty"
    }
  ]
}
```

Same ownership caveat as §2.2 applies.

## 3. Data model both sides must agree on

Full collection definitions live with the Ktor/MongoDB side, but both repos
read or write these shapes, so keep this section in sync too.

### Attempt (immutable — Ktor writes, never mutates)

```json
{
  "_id": "attempt001",
  "studentId": "user123",
  "questionId": "q101",
  "skillIds": ["linear-equation"],
  "answer": "B",
  "correct": true,
  "responseTimeMs": 18400,
  "attemptNumber": 3,
  "difficulty": 0.65,
  "timestamp": "..."
}
```

### KT State (FastAPI reads/writes directly; MongoDB is source of truth)

```json
{
  "studentId": "user123",
  "modelVersion": "gru-dkt-v1",
  "hiddenState": "...",
  "updatedAt": "..."
}
```

If the hidden-state representation grows large, the live copy is kept in
Redis (`student:{id}:kt_state`, holding `model_version`, `hidden_state`,
`sequence_length`, `last_attempt`), with MongoDB remaining the durable
source of truth. Redis is written/read by **Ktor only** — FastAPI talks to
MongoDB directly, not to Redis.

### Student Skill State (derived)

```json
{
  "studentId": "user123",
  "skillId": "linear-equation",
  "mastery": 0.81,
  "confidence": 0.73,
  "attempts": 17,
  "correct": 14,
  "incorrect": 3,
  "avgResponseTimeMs": 21000,
  "lastAttemptAt": "...",
  "modelVersion": "gru-dkt-v1"
}
```

## 4. Mastery bands (product config, not a model constant)

| Range | Category |
|---|---|
| 0.00 – 0.30 | Needs Attention |
| 0.30 – 0.50 | Developing |
| 0.50 – 0.70 | Intermediate |
| 0.70 – 0.85 | Strong |
| 0.85 – 1.00 | Mastered |

These thresholds are tunable per course without retraining the model — if
you're editing them, you're editing config on the Ktor side, not the model
on the FastAPI side.

## 5. Model versioning contract

- `modelVersion` (e.g. `gru-dkt-v1`, `gru-dkt-v2`) must be present on every
  KT State document and every `/v1/predict` response.
- A hidden state produced under one version is invalid input to a different
  version. Upgrading a model version requires either migrating stored
  states or recomputing each student's state from their full interaction
  history under the new version — FastAPI's responsibility, but Ktor should
  surface `modelVersion` mismatches rather than silently trusting a stale
  state.

## 6. Change process

Any change to a request/response shape here is a breaking-change candidate
for the other repo. When an agent changes one side:

1. Update this file's copy in both repos in the same change.
2. Call out the change explicitly (don't bury a payload-shape change inside
   an unrelated refactor).
3. If versioning the internal API is warranted, prefer adding a new version
   path (e.g. `/v2/predict`) over silently changing `/v1/predict`'s shape.
