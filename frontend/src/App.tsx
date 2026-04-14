import { useCallback, useEffect, useMemo, useState } from "react";
import { ChevronDown, ChevronRight, Save, Search, Sparkles, Trash2 } from "lucide-react";
import {
  createNote,
  deleteNote,
  fetchBacklinks,
  fetchNote,
  fetchNotes,
  fetchNoteTypes,
  fetchRecommendations,
  searchNotes,
  updateNote
} from "@/lib/api";
import type { Note, Recommendation } from "@/lib/types";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { GraphView } from "@/components/graph-view";

interface FormState {
  type: string;
  title: string;
  tags: string[];
  tagInput: string;
  links: string;
  content: string;
}

const emptyForm: FormState = {
  type: "",
  title: "",
  tags: [],
  tagInput: "",
  links: "",
  content: ""
};

function slugifyTitle(title: string) {
  const normalized = title
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9\s-]/g, "")
    .replace(/\s+/g, "-")
    .replace(/-+/g, "-")
    .replace(/^-|-$/g, "");
  return normalized || "untitled";
}

function toFormState(note: Note): FormState {
  return {
    type: note.type,
    title: note.title,
    tags: [...note.tags],
    tagInput: "",
    links: note.links.join(","),
    content: note.content
  };
}

function parseCsv(value: string) {
  return value
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function parsePathKey(pathKey: string) {
  const [type, fileName] = pathKey.split("/", 2);
  if (!type || !fileName) {
    throw new Error(`잘못된 노트 식별자: ${pathKey}`);
  }
  return { type, fileName };
}

export default function App() {
  const [activeTab, setActiveTab] = useState<"notes" | "graph">("notes");
  const [types, setTypes] = useState<string[]>([]);
  const [notes, setNotes] = useState<Note[]>([]);
  const [query, setQuery] = useState("");
  const [tag, setTag] = useState("");
  const [form, setForm] = useState<FormState>(emptyForm);
  const [selectedPathKey, setSelectedPathKey] = useState<string | null>(null);
  const [backlinks, setBacklinks] = useState<Note[]>([]);
  const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
  const [foldState, setFoldState] = useState<Record<string, boolean>>({});
  const [status, setStatus] = useState("초기 로딩 중...");

  const groupedNotes = useMemo(() => {
    const grouped: Record<string, Note[]> = {};
    for (const type of types) {
      grouped[type] = [];
    }
    for (const note of notes) {
      if (!grouped[note.type]) {
        grouped[note.type] = [];
      }
      grouped[note.type].push(note);
    }
    return grouped;
  }, [notes, types]);

  const autoFileName = useMemo(() => slugifyTitle(form.title), [form.title]);
  const currentPathKey = useMemo(() => {
    if (!form.type) {
      return "";
    }
    return `${form.type}/${autoFileName}`;
  }, [form.type, autoFileName]);

  const setMessage = (message: string) => setStatus(message);

  const loadNotes = useCallback(async () => {
    const loaded = await fetchNotes();
    setNotes(loaded);
    setMessage(`${loaded.length}개 노트 로드 완료`);
    return loaded;
  }, []);

  useEffect(() => {
    const bootstrap = async () => {
      try {
        const loadedTypes = await fetchNoteTypes();
        setTypes(loadedTypes);
        setFoldState(Object.fromEntries(loadedTypes.map((type) => [type, true])));
        setForm((prev) => ({ ...prev, type: prev.type || loadedTypes[0] || "" }));
        await loadNotes();
      } catch (error) {
        setMessage(error instanceof Error ? error.message : "초기화 실패");
      }
    };
    void bootstrap();
  }, [loadNotes]);

  const loadSelectedNoteDetails = useCallback(async (pathKey: string) => {
    const { type, fileName } = parsePathKey(pathKey);
    const detailed = await fetchNote(type, fileName);
    setForm(toFormState(detailed));
    setSelectedPathKey(detailed.id);
    const linkedBack = await fetchBacklinks(type, fileName);
    setBacklinks(linkedBack);
    setRecommendations([]);
    setMessage(`${detailed.id} 선택됨`);
  }, []);

  const selectNote = async (note: Note) => {
    try {
      await loadSelectedNoteDetails(note.id);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "노트 조회 실패");
    }
  };

  const executeSearch = async () => {
    try {
      const searched = await searchNotes(query, tag);
      setNotes(searched);
      setMessage(`검색 결과 ${searched.length}건`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "검색 실패");
    }
  };

  const clearEditor = () => {
    setForm((prev) => ({
      ...emptyForm,
      type: prev.type || types[0] || ""
    }));
    setSelectedPathKey(null);
    setBacklinks([]);
    setRecommendations([]);
  };

  const addTag = () => {
    const candidate = form.tagInput.trim();
    if (!candidate) {
      return;
    }
    const normalized = candidate.toLowerCase();
    const exists = form.tags.some((item) => item.toLowerCase() === normalized);
    if (exists) {
      setForm((prev) => ({ ...prev, tagInput: "" }));
      setMessage("중복 태그는 추가되지 않습니다.");
      return;
    }
    setForm((prev) => ({ ...prev, tags: [...prev.tags, candidate], tagInput: "" }));
  };

  const removeTag = (tagToRemove: string) => {
    setForm((prev) => ({
      ...prev,
      tags: prev.tags.filter((value) => value !== tagToRemove)
    }));
  };

  const handleSave = async () => {
    if (!form.type) {
      setMessage("노트 타입을 선택하세요.");
      return;
    }
    if (!form.title.trim()) {
      setMessage("제목을 입력하세요.");
      return;
    }

    const payload = {
      type: form.type,
      fileName: autoFileName,
      title: form.title.trim(),
      tags: form.tags,
      links: parseCsv(form.links),
      content: form.content
    };

    try {
      const targetExists = notes.some((note) => note.id === currentPathKey);
      if (targetExists) {
        await updateNote(payload);
        setMessage("노트 수정 완료");
      } else {
        await createNote(payload);
        setMessage("노트 생성 완료");
      }
      await loadNotes();
      await loadSelectedNoteDetails(currentPathKey);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "저장 실패");
    }
  };

  const handleDelete = async () => {
    const targetPathKey = selectedPathKey || currentPathKey;
    if (!targetPathKey) {
      setMessage("삭제할 노트를 선택하세요.");
      return;
    }

    try {
      const { type, fileName } = parsePathKey(targetPathKey);
      await deleteNote(type, fileName);
      await loadNotes();
      clearEditor();
      setMessage("노트 삭제 완료");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "삭제 실패");
    }
  };

  const handleRecommend = async () => {
    const targetPathKey = selectedPathKey || currentPathKey;
    if (!targetPathKey) {
      setMessage("추천할 노트를 먼저 저장하거나 선택하세요.");
      return;
    }

    try {
      const { type, fileName } = parsePathKey(targetPathKey);
      const rec = await fetchRecommendations(type, fileName);
      setRecommendations(rec);
      setMessage(`추천 ${rec.length}건`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "추천 실패");
    }
  };

  const toggleFold = (type: string) => {
    setFoldState((prev) => ({ ...prev, [type]: !prev[type] }));
  };

  const saveHint = notes.some((note) => note.id === currentPathKey) ? "저장(수정)" : "저장(생성)";

  return (
    <main className="min-h-screen bg-grid px-3 py-4 md:px-4">
      <div className="mx-auto w-full max-w-none space-y-4">
        <Card className="border-0 bg-white/90 shadow-xl backdrop-blur">
          <CardHeader className="pb-4">
            <CardTitle className="text-2xl">Zettelkasten Studio</CardTitle>
            <CardDescription>shadcn/ui + React + Vite</CardDescription>
            <div className="mt-2 flex gap-2">
              <Button variant={activeTab === "notes" ? "default" : "secondary"} onClick={() => setActiveTab("notes")}>노트</Button>
              <Button variant={activeTab === "graph" ? "default" : "secondary"} onClick={() => setActiveTab("graph")}>그래프</Button>
              <Button variant="secondary" onClick={() => void loadNotes()}>전체 새로고침</Button>
            </div>
          </CardHeader>
        </Card>

        {activeTab === "graph" ? (
          <GraphView />
        ) : (
          <div className="grid gap-4 lg:grid-cols-[240px_minmax(0,1fr)_280px]">
            <Card className="border-0 bg-white/90 shadow-xl backdrop-blur">
              <CardHeader>
                <CardTitle className="text-base">탐색</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid gap-2">
                  <Input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="제목/내용/태그 검색" />
                  <Input value={tag} onChange={(event) => setTag(event.target.value)} placeholder="태그 exact" />
                  <Button className="gap-2" onClick={executeSearch}>
                    <Search className="h-4 w-4" /> 검색
                  </Button>
                </div>

                <div id="noteList" className="space-y-2 rounded-lg border border-border bg-muted/30 p-2">
                  {types.map((type) => (
                    <Collapsible key={type} open={foldState[type]} onOpenChange={() => toggleFold(type)}>
                      <CollapsibleTrigger asChild>
                        <Button variant="ghost" className="w-full justify-between px-2">
                          <span className="text-xs font-semibold">{type}</span>
                          {foldState[type] ? <ChevronDown className="h-4 w-4" /> : <ChevronRight className="h-4 w-4" />}
                        </Button>
                      </CollapsibleTrigger>
                      <CollapsibleContent className="space-y-1 px-2 pb-2">
                        {(groupedNotes[type] ?? []).length === 0 ? (
                          <p className="rounded-md bg-white p-2 text-xs text-muted-foreground">노트 없음</p>
                        ) : (
                          groupedNotes[type].map((note) => (
                            <button
                              key={note.id}
                              className="w-full rounded-md border border-border bg-white p-2 text-left hover:border-primary"
                              onClick={() => void selectNote(note)}
                            >
                              <p className="text-sm font-medium">{note.title || note.fileName}</p>
                              <p className="text-xs text-muted-foreground">{note.id}</p>
                            </button>
                          ))
                        )}
                      </CollapsibleContent>
                    </Collapsible>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card className="border-0 bg-white/90 shadow-xl backdrop-blur">
              <CardHeader>
                <CardTitle>노트 편집</CardTitle>
                <CardDescription>제목 우선 + compact property</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-1">
                  <label className="text-sm font-medium">제목</label>
                  <Input id="title" value={form.title} onChange={(event) => setForm((prev) => ({ ...prev, title: event.target.value }))} placeholder="노트 제목" />
                </div>

                <div className="space-y-2 rounded-lg border border-border bg-muted/30 p-3">
                  <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Properties</p>
                  <div className="grid gap-2 md:grid-cols-[200px_1fr]">
                    <div className="space-y-1">
                      <label className="text-xs text-muted-foreground">노트 타입</label>
                      <Select id="type" value={form.type} onChange={(event) => setForm((prev) => ({ ...prev, type: event.target.value }))}>
                        {types.map((type) => (
                          <option key={type} value={type}>
                            {type}
                          </option>
                        ))}
                      </Select>
                    </div>
                    <div className="space-y-1">
                      <label className="text-xs text-muted-foreground">파일 경로 (자동)</label>
                      <Input id="fileName" value={currentPathKey} readOnly className="bg-white/70" />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <label className="text-xs text-muted-foreground">태그 (Enter 확정)</label>
                    <div className="flex flex-wrap gap-2">
                      {form.tags.map((tagValue) => (
                        <button
                          key={tagValue}
                          type="button"
                          className="rounded-full border border-border bg-white px-2 py-1 text-xs hover:border-primary"
                          onClick={() => removeTag(tagValue)}
                          title="클릭하면 제거"
                          aria-label={`${tagValue} 태그 제거`}
                        >
                          {tagValue}
                        </button>
                      ))}
                    </div>
                    <Input
                      id="tags"
                      value={form.tagInput}
                      placeholder="태그 입력 후 Enter"
                      onChange={(event) => setForm((prev) => ({ ...prev, tagInput: event.target.value }))}
                      onKeyDown={(event) => {
                        if (event.key === "Enter") {
                          event.preventDefault();
                          addTag();
                        }
                      }}
                    />
                  </div>

                  <div className="space-y-1">
                    <label className="text-xs text-muted-foreground">링크 (pathKey, 쉼표)</label>
                    <Input id="links" value={form.links} onChange={(event) => setForm((prev) => ({ ...prev, links: event.target.value }))} />
                  </div>
                </div>

                <div className="space-y-1">
                  <label className="text-sm font-medium">내용</label>
                  <Textarea id="content" value={form.content} onChange={(event) => setForm((prev) => ({ ...prev, content: event.target.value }))} className="min-h-[56vh]" />
                </div>

                <div className="flex items-center gap-2">
                  <Button
                    type="button"
                    size="sm"
                    onClick={() => void handleSave()}
                    aria-label={saveHint}
                    title={saveHint}
                  >
                    <Save className="h-4 w-4" />
                  </Button>
                  <Button
                    type="button"
                    size="sm"
                    variant="secondary"
                    onClick={() => void handleDelete()}
                    aria-label="삭제"
                    title="삭제"
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                  <Button variant="secondary" className="gap-2" onClick={() => void handleRecommend()}>
                    <Sparkles className="h-4 w-4" /> 링크 추천
                  </Button>
                </div>
              </CardContent>
            </Card>

            <div className="space-y-4">
              <Card className="border-0 bg-white/90 shadow-xl backdrop-blur">
                <CardHeader>
                  <CardTitle className="text-base">백링크</CardTitle>
                </CardHeader>
                <CardContent id="backlinks" className="space-y-2">
                  {backlinks.length === 0 ? (
                    <p className="text-sm text-muted-foreground">결과 없음</p>
                  ) : (
                    backlinks.map((note) => (
                      <div key={note.id} className="rounded-md border border-border bg-white p-2">
                        <p className="text-sm font-medium">{note.title || note.fileName}</p>
                        <p className="text-xs text-muted-foreground">{note.id}</p>
                      </div>
                    ))
                  )}
                </CardContent>
              </Card>

              <Card className="border-0 bg-white/90 shadow-xl backdrop-blur">
                <CardHeader>
                  <CardTitle className="text-base">링크 추천</CardTitle>
                </CardHeader>
                <CardContent id="recommendations" className="space-y-2">
                  {recommendations.length === 0 ? (
                    <p className="text-sm text-muted-foreground">결과 없음</p>
                  ) : (
                    recommendations.map((item) => (
                      <div key={item.note.id} className="rounded-md border border-border bg-white p-2">
                        <p className="text-sm font-medium">{item.note.title || item.note.fileName}</p>
                        <p className="text-xs text-muted-foreground">
                          {item.note.id} · score {item.score.toFixed(3)}
                        </p>
                      </div>
                    ))
                  )}
                </CardContent>
              </Card>

              <p className="rounded-md bg-secondary p-2 text-xs text-secondary-foreground">{status}</p>
            </div>
          </div>
        )}
      </div>
    </main>
  );
}
