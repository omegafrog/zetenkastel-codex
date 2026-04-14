import type { GraphView, Note, Recommendation, UpsertNotePayload } from "./types";

async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(path, {
    headers: {
      "Content-Type": "application/json"
    },
    ...options
  });

  if (!response.ok) {
    let message = `HTTP ${response.status}`;
    try {
      const body = (await response.json()) as { error?: string };
      if (body.error) {
        message = body.error;
      }
    } catch {
      // Keep default message.
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null as T;
  }

  return (await response.json()) as T;
}

export async function fetchNoteTypes() {
  return api<string[]>("/api/note-types");
}

export async function fetchNotes() {
  return api<Note[]>("/api/notes");
}

export async function fetchNote(type: string, fileName: string) {
  return api<Note>(`/api/notes/${type}/${fileName}`);
}

export async function searchNotes(query: string, tag: string) {
  const q = encodeURIComponent(query);
  const tagQuery = encodeURIComponent(tag);
  return api<Note[]>(`/api/notes/search?q=${q}&tag=${tagQuery}`);
}

export async function createNote(payload: UpsertNotePayload) {
  return api<Note>("/api/notes?linkMode=RECOMMEND", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export async function updateNote(payload: UpsertNotePayload) {
  return api<Note>(`/api/notes/${payload.type}/${payload.fileName}?linkMode=RECOMMEND`, {
    method: "PUT",
    body: JSON.stringify(payload)
  });
}

export async function deleteNote(type: string, fileName: string) {
  return api<void>(`/api/notes/${type}/${fileName}`, {
    method: "DELETE"
  });
}

export async function fetchBacklinks(type: string, fileName: string) {
  return api<Note[]>(`/api/notes/${type}/${fileName}/backlinks`);
}

export async function fetchRecommendations(type: string, fileName: string) {
  return api<Recommendation[]>(`/api/notes/${type}/${fileName}/recommendations`);
}

export async function fetchGraphView() {
  return api<GraphView>("/api/graph");
}
