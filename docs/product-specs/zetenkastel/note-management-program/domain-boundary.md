# Properties
doc_path: docs/product-specs/zetenkastel/note-management-program/domain-boundary.md
owner: Codex
status: completed
last_updated: 2026-04-13:22:17

# Task Summary
- 단일 사용자 웹 노트 앱에서 제텐카스텔 노트를 로컬 파일에 저장/조회/탐색한다.

# Domain Boundary
- In Scope
- 노트 CRUD (10개 노트 타입)
- 경로+파일명 식별
- 태그/제목/내용 검색
- 백링크 조회
- 그래프 뷰
- 유사도 기반 링크 추천
- plain JavaScript UI + Spring Boot API

- Out of Scope
- 인증/인가
- 멀티 사용자 협업
- 클라우드 저장소/백업

# External Systems
- 없음 (로컬 파일 시스템만 사용)
