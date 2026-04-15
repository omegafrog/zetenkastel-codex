# Property Union (기존 + 템플릿 합산)

## Scope
- 기존 시스템 속성(코드 기준)
  - `frontend/src/lib/types.ts`
  - `src/main/java/com/zetenkastel/core/domain/Note.java`
  - `src/main/java/com/zetenkastel/core/adapter/LocalFileNoteRepository.java`
- 템플릿 속성
  - `docs/reference/template-*.md` (frontmatter + 본문 메타 필드)

## 1) Existing Core Properties (현재 저장/조회되는 속성)
- `id` (derived): `type/fileName`
- `type`
- `fileName`
- `title`
- `tags[]`
- `links[]`
- `content`

## 2) Template-Derived Properties (템플릿에서 발견된 속성)
### Frontmatter keys
- `created`
- `updated`
- `tags`
- `status`
- `type`
- `aliases`
- `저자`
- `출처`
- `URL`
- `완료 여부`

### Body metadata keys
- `처리 예정일`
- `처리 기한`
- `날짜`
- `참석자`
- `장소/링크`
- `관련 프로젝트`
- `마지막 검토`
- `다음 검토`
- `상태`
- `기간`
- `마감일`
- `우선순위`
- `최종 업데이트`
- `노트 수`
- `키워드`
- `다음 작업`
- `블로커`

## 3) 합산 결과 (Deduplicated Union)
아래는 중복 제거 후 최종 속성 집합이다.

### A. Core (반드시 유지)
- `id` (derived)
- `type`
- `fileName`
- `title`
- `tags`
- `links`
- `content`

### B. Common Metadata (템플릿 공통)
- `created`
- `updated`
- `status`
- `aliases`

### C. Classification/Source Metadata
- `저자`
- `출처`
- `URL`
- `완료 여부`
- `키워드`

### D. Operational/Context Metadata
- `처리 예정일`
- `처리 기한`
- `날짜`
- `참석자`
- `장소/링크`
- `관련 프로젝트`
- `마지막 검토`
- `다음 검토`
- `상태`
- `기간`
- `마감일`
- `우선순위`
- `최종 업데이트`
- `노트 수`
- `다음 작업`
- `블로커`

## 4) Normalization 제안 (분류/자동화용)
자동 분류 스크립트에서 일관되게 쓰기 위해 아래 canonical key 매핑을 권장한다.

- `상태` -> `status`
- `완료 여부` -> `completion_status`
- `날짜` -> `meeting_date`
- `처리 예정일`, `처리 기한` -> `due_date`
- `관련 프로젝트` -> `related_projects`
- `마지막 검토` -> `last_reviewed_at`
- `다음 검토` -> `next_review_at`
- `장소/링크` -> `location_or_link`
- `기간` -> `date_range`

## 5) 타입별 최소 필요 속성 (초안)
- `inbox`: `title`, `content`, `tags`, `status`, `created`, `due_date`
- `fleeting`: `title`, `content`, `tags`, `status`, `created`, `due_date`
- `literature`: `title`, `content`, `tags`, `created`, `저자`, `출처`, `URL`, `type`, `완료 여부`
- `permanent`: `title`, `content`, `tags`, `created`, `updated`, `aliases`, `키워드`
- `project`: `title`, `content`, `tags`, `created`, `status`, `기간`, `마감일`, `우선순위`, `다음 작업`, `블로커`
- `area`: `title`, `content`, `tags`, `created`, `updated`, `상태`, `마지막 검토`, `다음 검토`
- `moc`: `title`, `content`, `tags`, `created`, `updated`, `최종 업데이트`, `노트 수`
- `meeting`: `title`, `content`, `tags`, `created`, `날짜`, `참석자`, `장소/링크`, `관련 프로젝트`

## 6) 구현 시 주의
- 현재 백엔드 저장 포맷은 `title/tags/links` + 본문만 지원한다. (`created`, `status` 등은 아직 저장되지 않음)
- 따라서 분류 정확도를 올리려면 아래 중 하나가 필요하다.
  - 본문에서 메타 필드 파싱
  - 저장 포맷 확장(헤더 key 추가)
  - YAML frontmatter 저장/파싱 도입
