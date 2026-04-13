# AGENTS.md

이 파일은 에이전트가 공통적으로 참고하는 최소 Source of Truth이다.
불필요한 정책, 설계 규칙, 워크플로우 설명은 포함하지 않는다.

---

## 1. Required Source of Truth

에이전트는 아래 순서로 설정을 참조한다:

1. `.codex/config.toml`
2. `.codex/openai.yaml`
3. `.codex/agents/*`
4. `docs/*`
5. 현재 코드베이스

AGENTS.md는 보조 입력이며, 없더라도 실행은 계속된다.

---

## 2. Build

프로젝트 빌드 방법:

```bash
./gradlew build
```

## 3. Test

테스트 실행 방법:

```bash
./gradlew test
```

## 4. Constraints

빌드 및 테스트는 실패 시 무시하지 않는다
테스트 없이 구현 완료로 간주하지 않는다
