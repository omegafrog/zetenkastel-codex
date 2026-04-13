# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/event-storming.md
owner: Codex
status: completed
last_updated: 2026-04-13:22:17

# Event Storming
- Commands
- CreateNote
- UpdateNote
- DeleteNote
- SearchNotes
- GetBacklinks
- GetGraph
- RecommendLinks

- Domain Events
- NoteCreated
- NoteUpdated
- NoteDeleted
- NotesSearched
- BacklinksCalculated
- GraphBuilt
- LinkRecommendationsGenerated

- Read Models
- NoteSummary
- NoteDetail
- GraphView
- Recommendation
