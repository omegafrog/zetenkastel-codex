---
name: orchestrate-plan
description: >
  use_case_harvester부터 closer까지 순차 실행하여
  유스케이스 수집, 설계, 문서화, 구현, 문서 동기화, 검증,
  완료 처리와 PR 정리까지 수행하는 오케스트레이션 스킬
---

# Orchestrate Plan

이 스킬은 사용자 작업 요청을 받아 다음 specialist agent들을 순서대로 실행한다.

1. use_case_harvester
2. oracle
3. doc_writer
4. doc_verify
5. executor
6. test_gate
7. execute_writer
8. doc_verify
9. closer

이 순서는 반드시 유지한다.

## Canonical Status Enums

### Harvest Gate

- coverage_gate: YES | PARTIAL | NO

### Verification Verdict

- PASS | FAIL | BLOCKED

### Test Gate Verdict

- PASS | FAIL | BLOCKED

### PR Readiness

- PR_READY | NOT_READY | BLOCKED

### Closure Verdict

- COMPLETED | STOPPED

## Source of Truth

이 스킬은 아래 설정을 source of truth로 따른다.

- `.codex/config.toml`
- `.codex/openai.yaml`
- `.codex/agents/use_case_harvester.toml`
- `.codex/agents/oracle.toml`
- `.codex/agents/doc_writer.toml`
- `.codex/agents/doc_verify.toml`
- `.codex/agents/executor.toml`
- `.codex/agents/test_gate.toml`
- `.codex/agents/execute_writer.toml`
- `.codex/agents/closer.toml`
- `.codex/contracts/workflow-contract.md`

설정 간 충돌이 있으면 `.codex/contracts/workflow-contract.md`를 우선한다.

최신 흐름은 다음이다.

`use_case_harvester → oracle → doc_writer → doc_verify → executor → test_gate → execute_writer → doc_verify → closer`

## Primary Goal

이 스킬의 목표는 다음을 end-to-end로 안전하게 오케스트레이션하는 것이다.

- 사용자 요청 해석
- use-case harvest 문서 생성 또는 갱신
- coverage gate 판정
- 설계 및 실행 계획 생성
- 설계 문서 기록
- 문서 검증
- 코드 구현
- 구현 결과 기반 문서 동기화
- 최종 문서 검증
- 완료 처리
- PR 생성 (draft 금지)

## Non-Goals

이 스킬은 다음을 하지 않는다.

- 선행 조건이 부족한데도 억지로 다음 단계를 진행하는 것
- 구현되지 않은 내용을 완료된 것처럼 보고하는 것
- 실패를 숨기고 계속 진행하는 것
- 없는 규칙을 임의로 만들어서 설계나 구현을 확장하는 것

## Global Rules

다음 규칙은 모든 단계에 적용된다.

- 사용자에게 추가 질문하지 않는다.
- 각 단계는 자기 agent 설정 파일의 규칙을 따른다.
- 실패한 단계는 숨기지 않는다.
- 선행 조건이 충족되지 않으면 즉시 중단한다.
- 모든 문서는 프로젝트 루트 기준 `doc_path`를 가져야 한다.
- `doc_path`는 경로+파일명을 포함해야 한다.
- 문서 식별에 id를 사용하지 않는다.
- 오케스트레이션 1회 실행은 하나의 work unit으로 기록한다.
- work unit 허브 문서는 `docs/work-units/<domain>/<task>/index.md`를 사용한다.
- stage 산출 문서는 work unit 허브로 backlink를 포함한다.
- executor는 반드시 `docs/exec-plans/active/<domain>/<task>/plan.md` 기준으로만 구현한다.
- execute_writer는 반드시 executor 결과와 실제 diff를 함께 기준으로 문서를 갱신한다.
- test_gate는 executor 이후 repository-specific 테스트를 실행하고 PASS일 때만 다음 단계로 진행한다.
- closer는 모든 완료 조건이 충족될 때만 완료 처리한다.

## Document Reference Rule

모든 핵심 문서는 `doc_path`로 식별한다.

형식:

`docs/<content-type>/<domain>/<task>/<file>.md`

규칙:

- `doc_path`는 프로젝트 루트 기준 상대경로여야 한다.
- 문서 id 필드는 사용하지 않는다.
- active/completed 이동 시 문서 식별은 이동된 `doc_path` 기준으로 판단한다.
- work unit 허브 문서는 `docs/work-units/<domain>/<task>/index.md`로 고정한다.
- 허브 문서는 각 stage 문서로 forward link를 가진다.
- 각 stage 문서는 허브 문서로 backlink를 가진다.

## Expected Document Paths

오케스트레이션 산출물은 반드시 아래 경로 규칙을 따른다.

- `docs/use-case-harvests/<domain>/<task>/use-case-harvest.md`
- `docs/work-units/<domain>/<task>/index.md`
- `docs/product-specs/<domain>/<task>/domain-boundary.md`
- `docs/product-specs/<domain>/<task>/use-cases.md`
- `docs/design-docs/<domain>/<task>/event-storming.md`
- `docs/design-docs/<domain>/<task>/aggregate-design.md`
- `docs/design-docs/<domain>/<task>/bounded-context.md`
- `docs/design-docs/<domain>/<task>/detailed-design.md`
- `docs/exec-plans/active/<domain>/<task>/plan.md`
- `docs/exec-plans/active/<domain>/<task>/implementation-log.md`

문서 탐색은 grep 패턴을 우선 사용하고, 보조 탐색으로 work unit 허브 backlink를 사용한다.

## Hard Gate Before Oracle And Downstream Steps

다음 조건이 모두 충족되지 않으면 `oracle` 및 그 이후 단계는 절대 시작하지 않는다.

1. `docs/use-case-harvests/<domain>/<task>/use-case-harvest.md` 가 존재한다.
2. 해당 문서의 `coverage_gate == YES`
3. 해당 문서의 `status == ready-for-oracle`
4. 사용자가 명시적으로 진행 승인했다.

위 조건 중 하나라도 만족하지 않으면 즉시 중단한다.

## Execution Procedure

### Step 1. Run use_case_harvester

목표:
사용자 요청을 해석해서 use-case harvest 문서를 생성하거나 갱신하고, coverage gate를 판정한다.

입력:

- 사용자 최신 요청
- 기존 `use-case-harvest.md` 가 있으면 그 문서
- 관련 docs
- 코드베이스

수행:

- `docs/use-case-harvests/<domain>/<task>/use-case-harvest.md` 를 생성 또는 갱신한다.
- `docs/work-units/<domain>/<task>/index.md`를 생성 또는 갱신한다.
- 문서에는 반드시 다음을 포함해야 한다.
  - `# Properties`
  - `doc_path`
  - `owner`
  - `status`
  - `title`
  - `domain`
  - `task`
  - `coverage_gate`
  - `next_step`
  - `last_updated`
- Candidate Use Cases와 Confirmed Use Cases를 분리한다.
- Coverage Mapping을 기록한다.
- Coverage Gate를 기록한다.
- Oracle Handoff 섹션을 기록한다.
- work unit index.md에 harvest 단계 상태와 문서 링크를 기록한다.

판정:

- `coverage_gate == NO` 또는 `PARTIAL` 이면 여기서 중단한다.
- `coverage_gate == YES` 이고 `status == ready-for-oracle` 이면 다음 gate check 대상이 된다.
- 단, 사용자 승인 전에는 반드시 중단한다.

### Step 2. Gate Check

아래를 검증한다.

- harvest 문서 존재 여부
- `coverage_gate == YES`
- `status == ready-for-oracle`
- 사용자 명시 승인 여부

하나라도 실패하면 즉시 중단한다.

중단 시 아래 형식을 따른다.

```md
# Orchestration Status
status: stopped
stopped_at: gate-check

# Reason
- <구체적 사유>

# Evidence
- <문서 경로 또는 상태 값>

# Next Required Action
- <다음에 무엇을 해야 하는지>
````

### Step 3. Run oracle

목표:
Confirmed Use Cases를 기반으로 설계 산출물과 실행 가능한 `plan.md` 초안을 만든다.

oracle은 반드시 다음을 산출해야 한다.

- Task Summary
- Assumptions
- Output Location
- Domain Boundary Candidate
- Confirmed Use Cases
- Event Storming
- Aggregate Design
- Bounded Context Finalization
- Detailed Design
- Implementation Plan
- Verification Plan
- Documentation Plan
- Output Files
- Plan.md Draft
- Design Tension
- Out of Scope

강제 규칙:

- use-case 수집을 다시 하지 않는다.
- Confirmed Use Cases만 사용한다.
- 모든 output file의 expected path를 프로젝트 루트 기준으로 명시한다.
- plan.md draft에는 반드시 Progress 체크박스가 있어야 한다.
- executor가 문맥 없이도 바로 작업할 수 있을 정도로 self-contained 해야 한다.

### Step 4. Run doc_writer

목표:
oracle 산출물을 실제 docs 파일로 구조화해 기록한다.

반드시 생성 또는 갱신 대상에 포함:

- `docs/work-units/<domain>/<task>/index.md`
- `docs/product-specs/<domain>/<task>/domain-boundary.md`
- `docs/product-specs/<domain>/<task>/use-cases.md`
- `docs/design-docs/<domain>/<task>/event-storming.md`
- `docs/design-docs/<domain>/<task>/aggregate-design.md`
- `docs/design-docs/<domain>/<task>/bounded-context.md`
- `docs/design-docs/<domain>/<task>/detailed-design.md`
- `docs/exec-plans/active/<domain>/<task>/plan.md`

강제 규칙:

- 각 문서는 자기 역할만 담는다.
- plan.md는 단일 진입점 역할을 유지한다.
- 문서 간 탐색은 grep 패턴을 우선한다.
- doc_path를 포함한다.
- 각 stage 문서는 `Backlinks` 섹션에서 work unit index를 가리킨다.
- 수동 메모가 있으면 근거 없이 삭제하지 않는다.

### Step 5. Run doc_verify

목표:
문서 구조, doc_path, metadata, role separation, cross-document consistency, freshness, implementation-log readiness, PR readiness를 검증한다.

검증 포인트:

- 필수 문서 존재 여부
- doc_path 존재 여부
- doc_path와 실제 파일 경로 일치 여부
- grep 기반 탐색 가능 여부
- work unit index 존재 여부
- index forward links와 stage 문서 backlinks 정합성
- 역할 분리 위반 여부
- use-case / event-storming / aggregate / bounded context / detailed design 사이의 정합성
- grep 기반 문서 탐색 힌트 유지 여부
- DDD 깊이 검증:
  - event-storming의 UC별 Start Command/Actors/External Systems/Events/Policies/Follow-up Commands/Sync-Async/Cross-Context 완전성
  - aggregate-design의 Aggregate별 Responsibility/Commands/Events/Entities-VO/Invariants/Transaction Rationale 완전성
  - detailed-design의 app/domain/port/adapter 분리 명시 여부
- ARCHITECTURE.md 계층 의존 방향(ui→app→domain, app→port, adapter→port) 문서/코드 정합성
- Gradle 기반 아키텍처 의존성 테스트 존재 및 실행 근거 여부

판정:

- PASS면 다음 단계 진행
- FAIL 또는 BLOCKED면 즉시 중단

중단 시 아래 형식을 따른다.

```md
# Orchestration Status
status: stopped
stopped_at: doc_verify_before_execute

# Reason
- document verification failed

# Evidence
- <실패 항목>
- <관련 경로>

# Next Required Action
- 문서 구조, doc_path, grep 탐색 정합성을 먼저 수정한다
```

### Step 6. Run executor

목표:
`docs/exec-plans/active/<domain>/<task>/plan.md` 기준으로 코드만 구현한다.

강제 규칙:

- 설계를 변경하지 않는다.
- plan.md에 없는 기능을 구현하지 않는다.
- Progress 체크박스 단위로 수행한다.
- 체크박스 완료 시 상태를 갱신한다.
- TDD 순서를 따른다.
- 최소 1개 이상의 핵심 유스케이스 테스트가 있어야 한다.
- 외부 시스템 호출은 테스트에서만 mocking 한다.
- 도메인 이벤트 로그를 남긴다.
- 필수 메트릭을 남긴다.
- 통합 테스트를 수행한다.
- plan.md의 `Properties.doc_path` 가 없으면 BLOCKED 처리한다.

반드시 남겨야 할 결과:

- Completed Steps
- Tests
- Logging Evidence
- Metrics Evidence
- External Mocking
- Issues / Design Tension

실패 시 숨기지 말고 즉시 중단한다.

### Step 7. Run test_gate

목표:
executor 이후 repository-specific 테스트를 실행하여 다음 단계 진행 가능 여부를 판정한다.

강제 규칙:

- `.codex/test-gate.yaml`이 존재하면 해당 stage를 순서대로 실행한다.
- `.codex/test-gate.yaml`이 없으면 `.codex/repository-settings.md` 또는 repo 자동탐지 규칙으로 커맨드를 결정한다.
- stage 하나라도 실패하면 FAIL로 즉시 중단한다.
- 환경/권한 문제로 실행 불가하면 BLOCKED로 즉시 중단한다.
- PASS일 때만 execute_writer 단계로 진행한다.

출력에는 반드시 아래를 포함한다:

- test_gate_verdict: PASS | FAIL | BLOCKED
- stage별 실행 커맨드
- stage별 핵심 로그 증거
- 다음 액션 (execute_writer 진행 또는 executor 복귀)

중단 시 아래 형식을 따른다.

```md
# Orchestration Status
status: stopped
stopped_at: test_gate

# Reason
- repository-specific test gate failed or blocked

# Evidence
- <stage name + command + 핵심 로그>

# Next Required Action
- executor 단계로 되돌아가 실패 원인을 수정한다
```

### Step 8. Run execute_writer

목표:
executor 결과와 실제 diff를 바탕으로 docs를 동기화하고 implementation-log를 작성 또는 갱신한다.

반드시 수행:

- plan.md Progress 최신화
- plan.md Documentation Impact 최신화
- plan.md Change Log 최신화
- `implementation-log.md` 생성 또는 갱신
- code-to-plan mapping 기록
- documentation updates 기록
- unresolved mismatches 기록
- work unit index.md에 실행/검증/완료 상태를 반영
- oracle/doc_writer에서 정의된 UC/Policy/Aggregate 구조를 요약/압축 없이 유지
- 템플릿 필수 섹션 누락 금지 (누락 시 `TBD` + 근거 기록)

필수 문서:

- `docs/exec-plans/active/<domain>/<task>/implementation-log.md`

implementation-log는 최소한 다음 섹션을 포함해야 한다.

- Properties
- Summary
- Implemented Scope
- File Changes
- Code-to-Plan Mapping
- External Contract Changes
- Policy / Domain Rule Changes
- Architectural Impact
- Documentation Updates
- Validation Summary
- Remaining Gaps
- Risks & Follow-ups

### Step 9. Run doc_verify Again

목표:
최종 상태의 docs가 code와 동기화되어 있는지, implementation-log와 traceability가 충분한지, PR 준비 상태인지 검증한다.

반드시 확인:

- implementation-log 존재 여부
- implementation-log 주요 섹션 존재 여부
- plan ↔ code ↔ docs traceability
- stale docs 없음
- plan Progress와 실제 구현 상태 일치 여부
- PR readiness 상태
- DDD 깊이/구조가 실행 후에도 유지되는지
- 아키텍처 의존성 테스트 결과(PASS/FAIL)와 위반 근거 경로

판정:

- PASS면 closer 진행
- FAIL 또는 BLOCKED면 즉시 중단

중단 시 아래 형식을 따른다.

```md
# Orchestration Status
status: stopped
stopped_at: doc_verify_after_execute

# Reason
- final document verification failed

# Evidence
- <실패 항목>
- <관련 경로>

# Next Required Action
- implementation-log, plan, docs traceability를 수정한다
```

### Step 10. Run closer

목표:
모든 완료 조건이 만족될 때만 작업을 completed로 전환하고 PR을 정리한다.

완료 조건:

1. 해당 세션의 `plan.md` checkbox가 모두 완료 상태다.
2. 최종 doc_verify 결과가 PASS 상태다.
3. test_gate 결과가 PASS 상태다.
4. PR readiness가 PR_READY 상태다.

수행:

- 관련 작업 문서 status를 completed로 갱신한다.
- 필요한 완료 메타데이터를 기록한다.
- work unit index.md에 closure verdict를 기록한다.
- `docs/exec-plans/active/<domain>/<task>/` 아래의 관련 실행 문서를 completed 경로로 이동한다.
- 이동 시 문서 id는 변경하지 않는다.
- 이동 후 grep 탐색성이 깨지면 문서 경로/탐색 힌트만 최소 수정한다.
- 코드 파일은 수정하지 않는다.
- PR 생성이 가능하면 생성한다.
- 실제 PR 생성이 불가능하면 즉시 사용할 수 있는 PR 제목과 본문을 만든다.

강제 규칙:

- 검증 실패 상태에서 완료 처리하지 않는다.
- 구현되지 않은 내용을 PR에 포함하지 않는다.
- plan 범위를 과장하지 않는다.

## Stop Rules

아래 중 하나라도 발생하면 즉시 중단한다.

- harvest gate 미통과
- 사용자 승인 없음
- pre-execution doc_verify FAIL 또는 BLOCKED
- executor BLOCKED 또는 실패
- test_gate FAIL 또는 BLOCKED
- post-execution doc_verify FAIL 또는 BLOCKED
- closer precondition 미충족

중단 시 반드시 아래 형식을 따른다.

```md
# Orchestration Status
status: stopped
stopped_at: <step-name>

# Reason
- <구체적 사유>

# Evidence
- <문서 경로, 검증 결과, 상태 값>

# Next Required Action
- <다음에 무엇을 고치거나 실행해야 하는지>
```

## Success Output Format

모든 단계가 정상 완료되면 반드시 아래 형식으로 요약한다.

```md
# Orchestration Status
status: completed

# Completed Stages
- use_case_harvester
- oracle
- doc_writer
- doc_verify
- executor
- test_gate
- execute_writer
- doc_verify
- closer

# Primary Output Paths
- docs/use-case-harvests/<domain>/<task>/use-case-harvest.md
- docs/work-units/<domain>/<task>/index.md
- docs/product-specs/<domain>/<task>/domain-boundary.md
- docs/product-specs/<domain>/<task>/use-cases.md
- docs/design-docs/<domain>/<task>/event-storming.md
- docs/design-docs/<domain>/<task>/aggregate-design.md
- docs/design-docs/<domain>/<task>/bounded-context.md
- docs/design-docs/<domain>/<task>/detailed-design.md
- docs/exec-plans/completed/<domain>/<task>/plan.md
- docs/exec-plans/completed/<domain>/<task>/implementation-log.md

# Verification Summary
- pre-execution doc verify: PASS
- test gate: PASS
- post-execution doc verify: PASS
- pr readiness: PR_READY

# Closure Result
- closure verdict: COMPLETED
- pr result: <created | body-prepared>
```

## Invocation Pattern

이 스킬은 다음 의도로 호출된다.

```text
Use the orchestrate-plan skill for this task:
<user request>
```

이 스킬은 항상 다음 순서를 따른다.

1. 먼저 harvest를 만든다.
2. gate를 확인한다.
3. gate 통과와 사용자 승인이 모두 확인되면 설계/문서/구현 파이프라인을 진행한다.
4. 각 단계 산출물을 다음 단계의 입력으로 사용한다.
5. 중간 실패를 숨기지 않는다.
