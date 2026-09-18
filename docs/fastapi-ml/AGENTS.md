# AGENTS.md (FastAPI + ML Service Guidance)

This file governs the **FastAPI/ML service side** described by the SRS.

## Current repository reality

- This `Quantum3600/IntelIQ` repository does **not** currently contain a FastAPI/Python service subtree.
- The SRS (`/SRS_Dynamic_Knowledge_Tracing_Platform-V2.docx`) defines FastAPI as an internal ML tier behind Ktor.
- Therefore, treat FastAPI implementation details below as **contract requirements for a separate or future subtree/repository**, not as code that already exists here.

## 1) FastAPI layering and ownership

Expected layering:

1. API layer (FastAPI routers + Pydantic validation)
2. Service layer (prediction/mastery/recommendation orchestration)
3. Model/runtime layer (feature encoding, model loading, inference)
4. Persistence/cache adapters as needed for KT state coordination

Ktor remains public/business authority; FastAPI remains internal inference authority.

## 2) Inference contracts

- Keep internal endpoints versioned (SRS examples include `/v1/predict`, `/v1/mastery`, recommend endpoint variants).
- Validate required features before inference (user/skill identifiers, correctness, response-time, difficulty where applicable).
- Return explicit `modelVersion` in inference outputs.
- Never expose raw hidden vectors or internals to external/public surfaces.

## 3) Model lifecycle and reproducibility

- Separate online inference from offline training.
- Training pipeline expectation: data validation → feature engineering → train → evaluate (AUC/ACC/RMSE + calibration) → register model → deploy.
- Use reproducible training runs (fixed seeds/config snapshots/dataset version references).
- Track experiment variants aligned with SRS ablation expectations.

## 4) Model versioning and state compatibility

- Multiple model versions may coexist (example: `gru-dkt-v1`, `gru-dkt-v2`).
- Hidden state compatibility is model-version-specific.
- On model upgrade, require either state migration or replay/recompute from interaction history before serving new-version state.

## 5) Explainability and feedback handling

- Recommendation responses should include concise student-facing reason text.
- Keep language descriptive (learning progress), not personal judgement.
- Log user feedback for recommendation quality without collecting unnecessary personal data.

## 6) Performance and reliability

- SRS target for inference latency: `p95 < 300ms` (engineering target).
- Handle empty/short/long sequence edge cases and unknown question/skill IDs safely.
- ML outage must not cause attempt loss: Ktor should persist attempts first and retry ML update.

## 7) Security and privacy

- FastAPI endpoints are internal-only (private network), never publicly exposed.
- Enforce service authentication/authorization between Ktor and FastAPI.
- Validate all payloads; reject malformed feature vectors.
- Protect student interaction data and avoid unnecessary retention.

## 8) Testing expectations

- Unit tests: feature encoder, predictor, mastery logic, recommendation scorer.
- Integration tests: Ktor ↔ FastAPI contract compatibility.
- ML-specific tests: sequence edge cases, missing values, extreme values, unknown entities.

## 9) Agent workflow constraints

- Do not change Ktor public contract and FastAPI internal contract in incompatible ways.
- If implementing FastAPI in another repo/subtree, include contract fixtures shared with Ktor.
- Document unresolved ownership/split-repo assumptions in PRs until FastAPI code is colocated or referenced explicitly.
