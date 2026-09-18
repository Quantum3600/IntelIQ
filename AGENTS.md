# AGENTS.md (KMP + Ktor Monorepo Guidance)

This repository is currently a **Kotlin Multiplatform monorepo** with a Ktor backend module.

- KMP app modules: `/app/androidApp`, `/app/desktopApp`, `/app/iosApp`, `/app/webApp`, `/app/shared`
- Shared domain module: `/core`
- Ktor backend module: `/server`
- Authoritative requirements: `/SRS_Dynamic_Knowledge_Tracing_Platform-V2.docx`

The SRS defines a larger architecture (`KMP + Ktor + FastAPI + MongoDB + Redis`).
**FastAPI/ML implementation is not present in this repository snapshot.** Treat that as an unresolved split-repository/subtree assumption and keep integration contracts explicit. See `docs/fastapi-ml/AGENTS.md`.

## 1) Architecture boundaries and ownership

- **KMP clients (`/app/*`)**: UI and client-side flow only. Do not move backend authority into clients.
- **Ktor (`/server`)**: business/API authority, request validation, authn/authz enforcement, durability/orchestration.
- **Shared Kotlin (`/core`, `/app/shared`)**: cross-platform contracts and reusable logic.
- **FastAPI ML service (external/absent here)**: inference/mastery/recommendation engine behind internal API boundaries.

Preserve this boundary: **public clients call Ktor, Ktor calls internal ML APIs**.

## 2) Build/test commands (verified from repository docs)

Use commands already documented in `/README.md`:

- Build Android app: `./gradlew :app:androidApp:assembleDebug`
- Run desktop app: `./gradlew :app:desktopApp:run`
- Run desktop hot reload: `./gradlew :app:desktopApp:hotRun --auto`
- Run web (Wasm): `./gradlew :app:webApp:wasmJsBrowserDevelopmentRun`
- Run web (JS): `./gradlew :app:webApp:jsBrowserDevelopmentRun`
- Run server: `./gradlew :server:run`

Tests:

- Android host tests: `./gradlew :app:shared:testAndroidHostTest`
- Desktop tests: `./gradlew :app:shared:jvmTest`
- Server tests: `./gradlew :server:test`
- Web tests: `./gradlew :app:shared:wasmJsTest` and `./gradlew :app:shared:jsTest`
- iOS simulator tests: `./gradlew :app:shared:iosSimulatorArm64Test`

## 3) API/data-contract rules from SRS

- Keep public Ktor API versioned under `/api/v1/...`.
- Preserve SRS attempt semantics: attempt events are immutable and include at least question, skills, correctness, response time, timestamp.
- Keep contract shape compatibility for expected flows:
  - `POST /api/v1/attempts` (public Ktor)
  - internal ML endpoints for prediction/mastery/recommendation (FastAPI side)
- Do not expose model internals (e.g., raw hidden state vectors) in public responses.

## 4) Authentication and authorization expectations

Per SRS NFR/security sections:

- Enforce authentication/authorization on every protected endpoint.
- Apply RBAC checks consistently (student/instructor/admin behavior).
- Rate-limit authentication endpoints.
- Prevent cross-student data access.

## 5) Offline/sync and failure behavior

- Student attempts must be persisted even if ML service is unavailable.
- If internal ML calls fail, return a graceful response indicating save succeeded and analysis will follow.
- Retriable ML update path should preserve eventual consistency without dropping attempts.

## 6) Observability and operations

- Keep structured logs around auth events, attempt ingestion, ML-call outcomes, and retry outcomes.
- Log security-relevant events.
- Track latency budgets from SRS as targets (`p95 API < 500ms`, `p95 inference < 300ms`).

## 7) Security and privacy constraints

- HTTPS-only transport in deployed environments.
- Validate all request bodies.
- Do not store plaintext credentials.
- Minimize student data collection to required KT fields.
- Never commit secrets, tokens, model artifacts with sensitive data, or production dumps.

## 8) Agent workflow constraints for this monorepo

- Keep changes scoped to touched module boundaries (`app`, `core`, `server`).
- Prefer contract-first changes when touching Ktor ↔ FastAPI interfaces.
- Add/update tests nearest to changed module.
- If a requirement depends on missing FastAPI code, document assumptions in PR and cross-reference `docs/fastapi-ml/AGENTS.md`.

For contributor workflow details, see `docs/agent-workflow.md`.
