# Agent Workflow (IntelIQ)

This workflow applies to coding agents working in this repository.

## 1) Discovery first

1. Read `/README.md`, Gradle module files, and relevant source directories.
2. Read `/SRS_Dynamic_Knowledge_Tracing_Platform-V2.docx` for authoritative product requirements.
3. Confirm whether target code exists in this repository or is an external/split component.

## 2) Plan and scope

- Keep changes surgical and module-local.
- Avoid unrelated refactors.
- For requirements touching missing FastAPI code, document assumptions and preserve Ktor contract compatibility.

## 3) Implement with contract safety

- KMP clients should not absorb backend authority.
- Ktor remains public API/business authority.
- Internal ML contract changes must be versioned and explicit.

## 4) Validate

Use verified commands from `/README.md` only. Select the smallest relevant command set:

- Server tests: `./gradlew :server:test`
- Shared/common tests: `./gradlew :app:shared:jvmTest` (or target-specific task as needed)

For documentation-only changes, validate links/paths and skip code tests unless required by policy.

## 5) PR expectations

PR descriptions should include:

- What changed and why
- Commands/tests run (or why not applicable)
- Any unresolved assumptions (especially Ktor/FastAPI split-repository ownership)
