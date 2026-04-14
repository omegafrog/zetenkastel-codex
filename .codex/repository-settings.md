# Properties
doc_path: .codex/repository-settings.md
owner: Codex
status: active
last_updated: 2026-04-14

# Purpose
- executor/test_gate가 repository-specific 실행 규칙을 동일하게 참조하기 위한 설정 문서

# Build & Test Commands
- build: `./gradlew build`
- unit/integration: `./gradlew test`
- e2e (Playwright): `./gradlew e2eTest`

# Test Policy
- 구현 완료 판정 전 최소 `./gradlew test` 통과 필요
- 오케스트레이션 게이트 통과 조건은 `.codex/test-gate.yaml`의 required stage 전부 PASS
- e2e는 Playwright(Chromium) 기준으로 실행

# Environment Notes
- `e2e/`는 Playwright workspace
- `./gradlew e2eTest`는 npm install + playwright browser install + e2e 실행을 포함
- 네트워크/권한 제한 환경에서는 browser install 단계가 BLOCKED 될 수 있음
