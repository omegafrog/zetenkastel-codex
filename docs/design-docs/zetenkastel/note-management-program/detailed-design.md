# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/detailed-design.md
owner: Codex
status: completed
last_updated: 2026-04-13:22:17

# Detailed Design
- Backend (Spring Boot)
- NoteController: /api/notes, /api/search, /api/backlinks, /api/graph, /api/links/recommend
- NoteService: 파일 CRUD, YAML front matter 파싱, 위키링크 추출, 추천 점수 계산(Jaccard)

- Frontend (plain JavaScript)
- index.html: 타입 목록, 노트 목록, 에디터, 검색, 그래프 섹션
- app.js: API 연동, CRUD 액션, 백링크/추천/그래프 렌더링
- styles.css: 반응형 레이아웃

# Error Handling
- API 오류 시 사용자 메시지 표시
- 파일 접근 실패 시 500 + 오류 메시지
- 잘못된 입력 시 400 + 오류 메시지
