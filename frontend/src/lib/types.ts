export interface Note {
  id: string;
  type: string;
  fileName: string;
  title: string;
  tags: string[];
  content: string;
  links: string[];
  metadata: Record<string, string>;
}

export interface Recommendation {
  note: Note;
  score: number;
}

export interface GraphNode {
  id: string;
  title: string;
  type: string;
}

export interface GraphEdge {
  source: string;
  target: string;
}

export interface GraphView {
  nodes: GraphNode[];
  edges: GraphEdge[];
}

export interface UpsertNotePayload {
  type: string;
  fileName: string;
  title: string;
  tags: string[];
  content: string;
  links: string[];
  metadata: Record<string, string>;
}
