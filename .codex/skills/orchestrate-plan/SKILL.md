---
name: orchestrate-plan
description: >
  request-classifying use-case-harvester → oracle → doc-writer → doc-verify
  → executor → test_gate → execute-writer → doc-verify → closer
  순으로 실행되는 전체 워크플로우 오케스트레이션 스킬
---

# Orchestrate Plan Skill

이 스킬은 전체 에이전트 워크플로우를 실행한다.

<<<<<<< HEAD
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
=======
---
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

## Source of Truth

다음 파일들을 단일 기준으로 따른다:

<<<<<<< HEAD
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
=======
1. `.codex/contracts/workflow-contract.md`
2. `.codex/stack-profile.yaml`
3. `.codex/test-gate.yaml`
4. `.codex/repository-settings.md`
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

충돌 시 우선순위:

1. workflow-contract.md
2. stack-profile.yaml
3. test-gate.yaml
4. repository-settings.md

<<<<<<< HEAD
`use_case_harvester → oracle → doc_writer → doc_verify → executor → test_gate → execute_writer → doc_verify → closer`
=======
---
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

## Core Principle

- 설계, 구현, 검증 흐름을 **순차적으로 실행**
- 각 단계는 이전 단계의 산출물을 입력으로 사용
- 모든 경로, 상태, 문서 identity는 workflow-contract 기준
- 기술 스택은 stack-profile.yaml을 단일 기준으로 사용
- 작업은 반드시 먼저 **Use Case / Non-Use-Case 변경**으로 분류한다

---

## Work Classification Rule (Critical)

오케스트레이션은 use_case_harvester가 먼저 아래 분류를 수행했다고 가정한다:

<<<<<<< HEAD
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
=======
- `UC`: 사용자/외부 시스템 행위 변화
- `UI`: 화면 구조, 상호작용, UX, 레이아웃 변화
- `TECH`: 리팩토링, 구조개선, 성능, 관측성, 내부 품질 변화
- `TEST`: 테스트 보강, 품질 게이트, 검증 체계 변화
- `DOC`: 문서 정리, 문서 동기화, 설명 보강

규칙:

- `UC`만 유스케이스로 취급한다.
- `UI/TECH/TEST/DOC`는 유스케이스로 승격하지 않는다.
- `UI/TECH/TEST/DOC`는 `Non-Use-Case Changes`로 유지한다.
- oracle/doc-writer/doc-verify/executor는 이 분리를 유지해야 한다.
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

---

## Gate Rule Before Oracle And Downstream Steps

<<<<<<< HEAD
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
=======
다음 중 하나를 만족해야 oracle 및 이후 단계로 진행할 수 있다:

### Case A. 기능 변화가 포함된 작업
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

1. harvest 문서 존재
2. coverage_gate == YES
3. status == ready-for-oracle
4. stack_profile_status == READY
5. `.codex/stack-profile.yaml` 존재
6. 사용자 명시적 승인 존재

### Case B. 기능 변화 없이 non-UC 작업만 있는 작업

1. harvest 문서 존재
2. non_uc_scope_status == READY
3. status == ready-for-oracle
4. stack_profile_status == READY
5. `.codex/stack-profile.yaml` 존재
6. 사용자 명시적 승인 존재

하나라도 만족하지 않으면:
→ 오케스트레이션 중단

---

## Workflow Steps

### Step 1. Run use_case_harvester

<<<<<<< HEAD
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
=======
- 요청을 분류한다:
  - UC
  - UI
  - TECH
  - TEST
  - DOC
- 다음 산출물 생성:
  - docs/use-case-harvests/<domain>/<task>/use-case-harvest.md
  - docs/work-units/<domain>/<task>/index.md
- `.codex/stack-profile.yaml` 상태 확인
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

판정:

- stack READY 아님 → STOP
- UC 포함 시 coverage_gate 기준 사용
- UC 없음 / non-UC only 시 non_uc_scope_status 기준 사용

---

### Step 2. Gate Check

검증:

- 기능 변경 포함 작업이면:
  - coverage_gate == YES
- 기능 변경 없는 작업이면:
  - non_uc_scope_status == READY
- status == ready-for-oracle
- stack_profile_status == READY
- stack-profile.yaml 존재
- 사용자 승인 여부

조건 미충족 시 STOP

---

### Step 3. Run oracle

입력:

- Confirmed Use Cases
- Non-Use-Case Changes
- stack-profile.yaml

출력 원칙:

- use-cases / event-storming / aggregate / bounded-context는 UC 중심
- detailed-design / plan.md는 UC + non-UC 전체 범위를 반영

---

### Step 4. Run doc-writer

- 설계 문서를 canonical 경로에 기록
- 역할 분리 유지:
  - use-cases.md에는 UC만
  - detailed-design.md에는 UI/TECH/TEST/DOC 포함 가능
  - plan.md에는 전체 실행 항목 포함
- non-UC only 작업이어도 canonical product-spec / design-doc 경로의 문서는 모두 생성한다
- 기능적으로 비해당인 문서는 `No functional use case change in this work unit` 또는 `N/A for this work unit` 같은 placeholder를 명시한다

<<<<<<< HEAD
- `docs/work-units/<domain>/<task>/index.md`
- `docs/product-specs/<domain>/<task>/domain-boundary.md`
- `docs/product-specs/<domain>/<task>/use-cases.md`
- `docs/design-docs/<domain>/<task>/event-storming.md`
- `docs/design-docs/<domain>/<task>/aggregate-design.md`
- `docs/design-docs/<domain>/<task>/bounded-context.md`
- `docs/design-docs/<domain>/<task>/detailed-design.md`
- `docs/exec-plans/active/<domain>/<task>/plan.md`
=======
---
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

### Step 5. Run doc-verify (before execute)

<<<<<<< HEAD
- 각 문서는 자기 역할만 담는다.
- plan.md는 단일 진입점 역할을 유지한다.
- 문서 간 탐색은 grep 패턴을 우선한다.
- doc_path를 포함한다.
- 각 stage 문서는 `Backlinks` 섹션에서 work unit index를 가리킨다.
- 수동 메모가 있으면 근거 없이 삭제하지 않는다.
=======
검증:
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

- UC와 non-UC 분리 유지 여부
- 역할 혼합 여부
- canonical path / doc_path / grep 탐색성

<<<<<<< HEAD
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
=======
---
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

### Step 6. Run executor

입력:

- plan.md
- stack-profile.yaml

역할:

- 코드 구현
- 테스트 작성
- plan의 작업 라벨(UC/UI/TECH/TEST/DOC) 유지

---

### Step 7. Run test_gate

<<<<<<< HEAD
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
=======
입력:
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

- stack-profile.yaml
- test-gate.yaml

FAIL/BLOCKED:
→ executor로 되돌림

<<<<<<< HEAD
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
=======
---
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

### Step 8. Run execute_writer

입력:

- executor 결과
- 실제 코드 diff
- stack-profile.yaml

역할:

<<<<<<< HEAD
### Step 9. Run doc_verify Again
=======
- 문서 동기화
- implementation-log 작성
- 실제 변경된 non-UC 항목 반영
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

---

### Step 9. Run doc-verify (after execute)

<<<<<<< HEAD
- implementation-log 존재 여부
- implementation-log 주요 섹션 존재 여부
- plan ↔ code ↔ docs traceability
- stale docs 없음
- plan Progress와 실제 구현 상태 일치 여부
- PR readiness 상태
- DDD 깊이/구조가 실행 후에도 유지되는지
- 아키텍처 의존성 테스트 결과(PASS/FAIL)와 위반 근거 경로
=======
검증:
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)

- 구현 결과와 문서 일치 여부
- non-UC 변경이 올바른 문서에 반영되었는지 확인

---

### Step 10. Run closer

- 작은 단위 커밋
- PR 생성

---

## Forbidden

<<<<<<< HEAD
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
- 실제 PR 생성이 불가능하면 즉시 사용할 수 있는 PR 제목과 본문 초안을 만든다.

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
- pr result: <created | draft-generated>
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
=======
- UI/TECH/TEST/DOC를 유스케이스로 강제 승격
- event-storming에 순수 리팩토링 항목 기록
- use-cases.md에 UI 레이아웃 변경 상세를 기록
- 기능 변화가 없는데 coverage_gate YES를 억지로 요구
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)
