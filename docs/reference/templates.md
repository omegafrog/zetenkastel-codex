# 09-Templates

## 목적
자주 사용하는 노트 형식을 템플릿으로 저장하여 일관성을 유지합니다.

## 기본 제공 템플릿
이 폴더에는 각 노트 타입별 템플릿이 저장됩니다:

- `template-fleeting-note.md` - 임시 메모 템플릿
- `template-literature-note.md` - 문헌 노트 템플릿
- `template-permanent-note.md` - 영구 노트 템플릿
- `template-project.md` - 프로젝트 노트 템플릿
- `template-area.md` - Area 노트 템플릿
- `template-moc.md` - MOC 템플릿
- `template-daily-note.md` - 데일리 노트 템플릿
- `template-meeting.md` - 회의록 템플릿

## 사용 방법

### Obsidian 템플릿 플러그인 설정
1. Settings → Core plugins → Templates 활성화
2. Templates → Template folder location: `09-templates`로 설정
3. 단축키 설정 (예: Ctrl/Cmd + T)

### 템플릿 사용
1. 새 노트 생성
2. 템플릿 단축키 실행
3. 원하는 템플릿 선택
4. 내용 작성

## 템플릿 변수
Obsidian에서 사용 가능한 변수:

- `{{title}}` - 노트 제목
- `{{date}}` - 오늘 날짜
- `{{time}}` - 현재 시간
- `{{date:YYYY-MM-DD}}` - 포맷 지정 날짜

## 커스텀 템플릿 추가
필요에 따라 자신만의 템플릿 생성:

```markdown
# {{title}}

생성일: {{date:YYYY-MM-DD}}

[템플릿 내용]
```

## Templater 플러그인
더 강력한 템플릿 기능이 필요하면 Templater 커뮤니티 플러그인 사용 권장:
- 동적 콘텐츠 생성
- JavaScript 사용
- 조건부 로직
- 프롬프트 입력
