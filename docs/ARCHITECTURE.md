# Architecture — KMP + Ktor Monorepo

Supplementary reference for agents; `AGENTS.md` in this repo is the primary
steering doc and takes precedence if anything here seems to conflict with it.

## System shape

Five cooperating components, three of which are outside this repo's code but
must be reasoned about when changing anything here:

```
KMP client (this repo)
   |
   v
Ktor backend (this repo)  <---> Redis (cache, ephemeral)
   |                       <---> MongoDB (durable source of truth)
   v
FastAPI ML service (separate repo, private network only)
   |
   v
MongoDB (hidden knowledge-tracing state — read/written directly by FastAPI)
```

FastAPI itself is stateless: the only persistent copy of a student's hidden
knowledge state lives in MongoDB, read and written directly by the ML service
on each call.

## Data ownership

| Component | Owns |
|---|---|
| KMP client | UI state, local cache, navigation state |
| Ktor | Business data, authorization, attempt persistence, orchestration of Redis, the database, and FastAPI |
| Redis | Fast, ephemeral data used directly by Ktor — sessions, rate limiting, response caching |
| MongoDB | All durable records, including each student's hidden knowledge-tracing state (source of truth) |
| FastAPI | Preprocessing logic and the GRU-based DKT model itself; stateless, holds nothing between calls |

Rule of thumb: if it needs to survive a restart, it's MongoDB's job. If it's
disposable and latency-sensitive, it's Redis's job. Ktor is the only thing in
this repo allowed to write to either.

## Consistency model

MongoDB is the single durable source of truth for both ordinary business
records and every student's hidden knowledge state. Redis sits beside Ktor
purely as a cache and is **never in the path of a knowledge-tracing update**
— don't make correctness of the KT pipeline depend on a Redis read succeeding.
FastAPI reads/writes the relevant hidden-state document in MongoDB directly
and returns its result synchronously so Ktor can respond to the client in the
same request.

## Deployment

### Local dev

```
docker-compose
|-- ktor
|-- fastapi
|-- mongodb
|-- redis
+-- nginx

KMP -> localhost:8080 -> Ktor -> FastAPI:8000
```

### Cloud

```
Internet -> Load Balancer -> [ Ktor #1, Ktor #2 ]
   -> internal network -> [ FastAPI #1, FastAPI #2 ] -> Redis / MongoDB
```

Ktor and FastAPI scale independently and horizontally. GPU machines can be
introduced later for the FastAPI tier if inference/training needs it — that
change is scoped to the ML repo's deployment, not this one.

## Why the split exists

The Ktor backend and the FastAPI ML service run as independently deployable
services communicating over a private network rather than as one monolithic
application. This is the single most consequential architectural decision in
the system: it lets the knowledge-tracing model be upgraded, retrained, or
scaled on its own timeline without touching the application backend or the
client. Any refactor that blurs this boundary (e.g. calling FastAPI code
in-process, or moving business logic into the ML service) undoes that
benefit and should be treated as a design regression, not a convenience.

## Reliability posture

An outage of the ML service must never cost a student their submitted
answer. If FastAPI is temporarily unreachable, Ktor still persists the
attempt immediately and queues the knowledge-tracing update for retry
(simple save-then-retry is acceptable for the MVP; a full queue is a later
concern). The API responds gracefully either way — never a bare 500 to the
student.
