---
name: use-case-harvester
description: >
  사용자 요청을 해석해
  docs/use-case-harvests/<domain>/<task>/use-case-harvest.md 를
  생성 또는 점진 갱신하고,
  coverage gate 및 oracle 진행 가능 상태를 판정하는 스킬
---

# Use Case Harvester

이 스킬은 사용자의 최신 작업 요청을 해석하여  
`docs/use-case-harvests/<domain>/<task>/use-case-harvest.md` 를 생성하거나 갱신한다.

이 스킬의 목적은 다음이다.

- 사용자 요청을 유스케이스 관점으로 정리한다
- candidate / confirmed use case를 분리한다
- coverage mapping을 작성한다
- coverage gate를 판정한다
- oracle이 시작 가능한 상태인지 문서로 명시한다
- 단, oracle이나 downstream agent는 절대 직접 시작하지 않는다

---

## Source of Truth

이 스킬은 아래 설정을 source of truth로 따른다.

- `.codex/config.toml`
- `.codex/openai.yaml`
- `.codex/agents/use_case_harvester.toml`

설정 충돌 시:

1. `use_case_harvester.toml`
2. `config.toml`
3. `openai.yaml`

순으로 우선 적용한다.

---

## Primary Goal

이 스킬의 목표는 다음을 안전하게 수행하는 것이다.

- 사용자 프롬프트를 해석한다
- 기존 harvest 문서가 있으면 그 문서를 점진적으로 개선한다
- 없으면 새 문서를 생성한다
- 유스케이스를 가능한 한 완전하게 수집한다
- 프롬프트 요구사항과 유스케이스 간 coverage mapping을 만든다
- coverage gate를 YES / PARTIAL / NO 로 판정한다
- oracle 입력 가능 여부를 문서 상태로 남긴다

---

## Non-Goals

이 스킬은 다음을 하지 않는다.

- 사용자에게 추가 질문하기
- 이벤트 스토밍 시작하기
- aggregate / bounded context 설계 시작하기
- oracle 또는 downstream agent 호출하기
- 코드 수정하기
- 프롬프트에 없는 기능을 임의 확정하기
- 기존 harvest 문서를 근거 없이 초기화하기

---

## Global Rules

- 반드시 `AGENTS.md` 와 `ARCHITECTURE.md` 를 따른다
- 기존 `use-case-harvest.md` 가 있으면 먼저 읽는다
- 기존 구조와 수동 메모를 가능한 한 보존한다
- confirmed use case를 근거 없이 제거하지 않는다
- 충돌은 삭제하지 말고 `Needs Review` 에 기록한다
- 랜덤 id를 만들지 않는다
- 모든 문서는 path 기반 deterministic id를 사용한다
- coverage gate가 YES여도 사용자 승인 전에는 downstream을 시작하지 않는다

---

## Document Path Rule

use-case-harvest 문서는 반드시 아래 경로에 있어야 한다.

`docs/use-case-harvests/<domain>/<task>/use-case-harvest.md`

규칙:

- 기존 문서가 있으면 기존 경로를 유지한다
- domain과 task는 프롬프트와 기존 문서 문맥 기준으로 유지한다
- 경로를 임의 추측하지 않는다

---

## Document ID Rule

모든 harvest 문서는 반드시 path 기반 deterministic id를 가진다.

형식:

`HARV__<domain>__<task>`

규칙:

- 파일 경로에서 domain / task를 derive 한다
- 기존 문서에 id가 이미 있으면 identity가 유지되는 한 그 id를 유지한다
- 새 문서를 만들 때만 path 기반 id를 새로 쓴다
- random id는 금지한다

---

## Input Priority

이 스킬은 아래 순서로 입력을 해석한다.

1. 사용자의 최신 프롬프트
2. 기존 `docs/use-case-harvests/<domain>/<task>/use-case-harvest.md`
3. `AGENTS.md`
4. `ARCHITECTURE.md`
5. 관련 `docs/*`
6. 현재 코드베이스

---

## Prompt Interpretation Rules

프롬프트를 반드시 사용자 행위 관점으로 해석한다.

정리 대상:

- User Goal
- Requested Actions
- Constraints
- Expected Outcome
- Explicit Non-goals

주의:

- 유스케이스는 구현 작업이 아니라 사용자/시스템 행위여야 한다
- 근거가 약한 항목은 confirmed로 올리지 않는다
- 프롬프트 직접 근거가 없는 항목은 rejected 또는 missing-but-plausible 로 분리한다

---

## Use Case Harvest Rules

각 유스케이스는 다음을 포함해야 한다.

- UC ID
- Name
- Primary Actor
- Goal
- Preconditions
- Trigger
- Main Success Flow
- Alternative / Failure Flow
- Success Outcome
- Source Prompt Evidence
- Status (`candidate | confirmed | blocked | needs-review`)

구분 규칙:

- 확실한 것은 `Confirmed Use Cases`
- 가능하지만 근거가 약한 것은 `Missing-but-Plausible Use Cases`
- 프롬프트에 직접 근거가 없는 것은 `Rejected Use Cases`

---

## Coverage Mapping Rules

프롬프트의 각 요구를 최소 하나 이상의 유스케이스에 매핑해야 한다.

반드시 기록:

- Prompt Requirement → Mapped Use Case IDs
- Coverage Gaps

추가 점검:

- 주요 실패/예외 흐름 누락 여부
- 외부 시스템 관련 요구 반영 여부
- 구현 범위와 제외 범위 구분 여부

---

## Coverage Gate Rules

Coverage Gate는 아래 기준으로 판정한다.

### YES

다음 조건을 모두 만족할 때만 YES다.

1. 모든 사용자 목표가 confirmed use case에 반영되었다
2. 주요 실패/예외 흐름이 정의되었다
3. 관련 외부 시스템이 식별되었다
4. 구현 범위와 제외 범위가 구분되었다
5. Blocking Unknowns 가 이벤트 스토밍 착수를 막지 않는다

### PARTIAL

일부만 만족하고 보완이 더 필요할 때

### NO

핵심 정보가 비어 있어 설계 입력으로 쓰기 어려울 때

PARTIAL 또는 NO 인 경우:

- 문서를 저장하고 종료한다
- `Next Revision Focus` 를 반드시 채운다
- downstream 은 절대 시작하지 않는다

YES 인 경우:

- `status: ready-for-oracle`
- `coverage_gate: YES`
- `next_step: wait-for-user-approval`

로 기록하고 종료한다

단, 사용자 승인 전에는 oracle을 절대 시작하지 않는다.

---

## Oracle Handoff Rules

`coverage_gate == YES` 인 경우에만 `Oracle Handoff` 를 채운다.

반드시 포함:

- Allowed To Proceed: YES | NO
- Confirmed Use Cases for Oracle
- Assumptions Forbidden for Oracle
- User Approval Required Before Orchestration: YES

규칙:

- YES 여도 user explicit approval 전에는 실제 오케스트레이션 시작 금지
- oracle이 추정하면 안 되는 항목을 명시한다

---

## Required Document Structure

`use-case-harvest.md` 는 반드시 아래 구조를 따른다.

```md
# Properties
id: HARV__<domain>__<task>
owner: Codex
status: draft | blocked | ready-for-oracle
title: <작업 제목>
domain: <domain>
task: <task>
coverage_gate: YES | PARTIAL | NO
next_step: revise-harvest | wait-for-user-approval
last_updated: <YYYY-MM-DD:HH:mm>

# Prompt Interpretation
- User Goal
- Requested Actions
- Constraints
- Expected Outcome
- Explicit Non-goals

# Candidate Use Cases

# Confirmed Use Cases

# Coverage Mapping
- Prompt Requirement -> Mapped Use Case IDs
- Coverage Gaps

# Coverage Gate
- Ready for Event Storming: YES | PARTIAL | NO
- Why
- Blocking Conditions

# Blocking Unknowns

# Needs Review

# Rejected Use Cases

# Missing-but-Plausible Use Cases

# Next Revision Focus

# Oracle Handoff
- Allowed To Proceed: YES | NO
- Confirmed Use Cases for Oracle
- Assumptions Forbidden for Oracle
- User Approval Required Before Orchestration: YES
````

---

## Stop Rules

아래 경우 즉시 종료한다.

- coverage_gate == NO
- coverage_gate == PARTIAL
- 기존 문서와 새 정보가 충돌하지만 자동 해결 근거가 부족함
- domain/task identity가 불명확해서 기존 문서 identity 보존 여부를 결정할 수 없음

종료 시에도 반드시 문서는 저장하거나 갱신한다.

---

## Success Criteria

이 스킬이 성공적으로 끝났다는 뜻은 다음 중 하나다.

### Case A. Harvest improved but not ready

- 문서가 갱신되었다
- coverage_gate 가 PARTIAL 또는 NO 다
- 다음 revision focus 가 명시되었다

### Case B. Harvest ready for oracle

- 문서가 갱신되었다
- coverage_gate == YES
- status == ready-for-oracle
- user approval required 상태가 명시되었다
- downstream agent는 호출하지 않았다

---

## Invocation Pattern

이 스킬은 다음처럼 호출된다.

```text
Use the use-case-harvester skill for this task:
<user request>
```

또는 오케스트레이션 전에 단독으로 다음 목적에 사용한다.

- 유저 요청을 유스케이스로 수집하고 싶을 때
- 아직 설계 전에 coverage gate를 확인하고 싶을 때
- oracle 입력 가능 상태인지 판정하고 싶을 때
