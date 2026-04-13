const NOTE_TYPES = [
  ["inbox", "inbox"],
  ["fleeting_notes", "fleeting notes"],
  ["literature_notes", "literature notes"],
  ["projects", "projects"],
  ["area", "area"],
  ["archives", "archives"],
  ["maps_of_content", "maps-of-content"],
  ["references", "references"],
  ["templates", "templates"],
  ["attachments", "attachments"]
];

let currentType = "inbox";
let currentPath = "";

function qs(id) { return document.getElementById(id); }

async function api(url, options = {}) {
  const res = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options
  });
  if (!res.ok) {
    let message = `API Error ${res.status}`;
    try {
      const data = await res.json();
      message = data.error || message;
    } catch (_) {}
    throw new Error(message);
  }
  return res.json();
}

function renderTypes() {
  const ul = qs("typeList");
  const select = qs("type");
  ul.innerHTML = "";
  select.innerHTML = "";

  NOTE_TYPES.forEach(([value, label]) => {
    const li = document.createElement("li");
    li.textContent = label;
    if (value === currentType) li.classList.add("active");
    li.onclick = () => {
      currentType = value;
      renderTypes();
      loadNotes();
    };
    ul.appendChild(li);

    const op = document.createElement("option");
    op.value = value;
    op.textContent = label;
    if (value === currentType) op.selected = true;
    select.appendChild(op);
  });
}

function clearEditor() {
  currentPath = "";
  qs("path").value = "";
  qs("title").value = "";
  qs("tags").value = "";
  qs("content").value = "";
  qs("backlinks").textContent = "백링크 없음";
  qs("recommendations").textContent = "추천 없음";
  qs("type").value = currentType;
}

async function loadNotes() {
  qs("listTitle").textContent = `Notes: ${currentType}`;
  const notes = await api(`/api/notes?type=${encodeURIComponent(currentType)}`);
  const ul = qs("noteList");
  ul.innerHTML = "";
  notes.forEach(n => {
    const li = document.createElement("li");
    li.textContent = `${n.title} (${n.path})`;
    if (n.path === currentPath) li.classList.add("active");
    li.onclick = () => openNote(n.path);
    ul.appendChild(li);
  });
  await loadGraph();
}

async function openNote(path) {
  const note = await api(`/api/notes/detail?path=${encodeURIComponent(path)}`);
  currentPath = note.path;
  qs("type").value = note.type;
  qs("path").value = note.path;
  qs("title").value = note.title;
  qs("tags").value = note.tags.join(",");
  qs("content").value = note.content;
  await loadBacklinks(note.path);
  await loadRecommendations(note.path);
  await loadNotes();
}

async function loadBacklinks(path) {
  const items = await api(`/api/backlinks?path=${encodeURIComponent(path)}`);
  qs("backlinks").innerHTML = items.length
    ? `백링크: ${items.map(i => `<a href="#" data-path="${i.path}">${i.title}</a>`).join(", ")}`
    : "백링크 없음";
  qs("backlinks").querySelectorAll("a").forEach(a => {
    a.onclick = (e) => {
      e.preventDefault();
      openNote(a.dataset.path);
    };
  });
}

async function loadRecommendations(path) {
  const items = await api(`/api/links/recommend?path=${encodeURIComponent(path)}&limit=5`);
  qs("recommendations").innerHTML = items.length
    ? `추천: ${items.map(i => `<a href="#" data-path="${i.path}">${i.title}</a> (${i.score.toFixed(2)})`).join(", ")}`
    : "추천 없음";
  qs("recommendations").querySelectorAll("a").forEach(a => {
    a.onclick = (e) => {
      e.preventDefault();
      openNote(a.dataset.path);
    };
  });
}

async function createNote() {
  const body = {
    type: qs("type").value,
    title: qs("title").value,
    tags: qs("tags").value.split(",").map(v => v.trim()).filter(Boolean),
    content: qs("content").value
  };
  const note = await api("/api/notes", { method: "POST", body: JSON.stringify(body) });
  currentType = note.type;
  await openNote(note.path);
}

async function saveNote() {
  if (!currentPath) {
    alert("먼저 노트를 선택하거나 생성하세요.");
    return;
  }
  const body = {
    path: currentPath,
    title: qs("title").value,
    tags: qs("tags").value.split(",").map(v => v.trim()).filter(Boolean),
    content: qs("content").value
  };
  const note = await api("/api/notes", { method: "PUT", body: JSON.stringify(body) });
  await openNote(note.path);
}

async function deleteNote() {
  if (!currentPath) return;
  if (!confirm("삭제할까요?")) return;
  await api(`/api/notes?path=${encodeURIComponent(currentPath)}`, { method: "DELETE" });
  clearEditor();
  await loadNotes();
}

async function doSearch() {
  const q = qs("q").value;
  const tag = qs("tag").value;
  const notes = await api(`/api/search?q=${encodeURIComponent(q)}&tag=${encodeURIComponent(tag)}`);
  const ul = qs("noteList");
  qs("listTitle").textContent = "Search Results";
  ul.innerHTML = "";
  notes.forEach(n => {
    const li = document.createElement("li");
    li.textContent = `${n.title} (${n.path})`;
    li.onclick = () => openNote(n.path);
    ul.appendChild(li);
  });
}

async function loadGraph() {
  const graph = await api("/api/graph");
  const svg = qs("graph");
  const width = svg.clientWidth || 1200;
  const height = svg.clientHeight || 380;
  svg.innerHTML = "";

  const radius = Math.min(width, height) / 2 - 40;
  const centerX = width / 2;
  const centerY = height / 2;
  const positions = {};

  graph.nodes.forEach((n, i) => {
    const angle = (2 * Math.PI * i) / Math.max(graph.nodes.length, 1);
    positions[n.id] = {
      x: centerX + radius * Math.cos(angle),
      y: centerY + radius * Math.sin(angle)
    };
  });

  graph.edges.forEach(e => {
    const s = positions[e.source];
    const t = positions[e.target];
    if (!s || !t) return;
    const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
    line.setAttribute("x1", s.x); line.setAttribute("y1", s.y);
    line.setAttribute("x2", t.x); line.setAttribute("y2", t.y);
    svg.appendChild(line);
  });

  graph.nodes.forEach(n => {
    const p = positions[n.id];
    if (!p) return;

    const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    circle.setAttribute("cx", p.x); circle.setAttribute("cy", p.y); circle.setAttribute("r", 7);
    circle.style.cursor = "pointer";
    circle.onclick = () => openNote(n.id);
    svg.appendChild(circle);

    const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
    text.setAttribute("x", p.x + 10); text.setAttribute("y", p.y - 8);
    text.textContent = n.label;
    svg.appendChild(text);
  });
}

async function boot() {
  renderTypes();
  clearEditor();
  await loadNotes();

  qs("newBtn").onclick = clearEditor;
  qs("createBtn").onclick = () => createNote().catch(e => alert(e.message));
  qs("saveBtn").onclick = () => saveNote().catch(e => alert(e.message));
  qs("deleteBtn").onclick = () => deleteNote().catch(e => alert(e.message));
  qs("recommendBtn").onclick = () => {
    if (!currentPath) return;
    loadRecommendations(currentPath).catch(e => alert(e.message));
  };
  qs("searchBtn").onclick = () => doSearch().catch(e => alert(e.message));
  qs("resetBtn").onclick = () => {
    qs("q").value = "";
    qs("tag").value = "";
    loadNotes().catch(e => alert(e.message));
  };
}

boot().catch(e => alert(e.message));
