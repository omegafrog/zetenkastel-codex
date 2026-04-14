import { useEffect, useMemo, useState } from "react";
import { fetchGraphView } from "@/lib/api";
import type { GraphView as GraphPayload } from "@/lib/types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

interface PositionedNode {
  id: string;
  title: string;
  x: number;
  y: number;
}

const WIDTH = 960;
const HEIGHT = 560;

export function GraphView() {
  const [data, setData] = useState<GraphPayload>({ nodes: [], edges: [] });
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchGraphView()
      .then((next) => {
        setData(next);
        setError(null);
      })
      .catch((err) => {
        setError(err instanceof Error ? err.message : "그래프 로드 실패");
      });
  }, []);

  const nodes = useMemo<PositionedNode[]>(() => {
    return data.nodes.map((node, index) => {
      const total = Math.max(1, data.nodes.length);
      const angle = (Math.PI * 2 * index) / total;
      return {
        id: node.id,
        title: node.title || node.id,
        x: WIDTH / 2 + 320 * Math.cos(angle),
        y: HEIGHT / 2 + 200 * Math.sin(angle)
      };
    });
  }, [data.nodes]);

  const nodeById = useMemo(() => new Map(nodes.map((node) => [node.id, node])), [nodes]);

  return (
    <Card className="h-full">
      <CardHeader>
        <CardTitle>Graph View</CardTitle>
      </CardHeader>
      <CardContent>
        {error ? <p className="text-sm text-red-700">{error}</p> : null}
        <div className="overflow-auto rounded-lg border border-border bg-white">
          <svg viewBox={`0 0 ${WIDTH} ${HEIGHT}`} className="h-[560px] w-full min-w-[920px]">
            {data.edges.map((edge) => {
              const source = nodeById.get(edge.source);
              const target = nodeById.get(edge.target);
              if (!source || !target) {
                return null;
              }
              return (
                <line
                  key={`${edge.source}-${edge.target}`}
                  x1={source.x}
                  y1={source.y}
                  x2={target.x}
                  y2={target.y}
                  stroke="#64748b"
                  strokeWidth="1.4"
                />
              );
            })}
            {nodes.map((node) => (
              <g key={node.id}>
                <circle cx={node.x} cy={node.y} r={16} fill="#0f766e" opacity="0.9" />
                <text x={node.x + 22} y={node.y + 4} fontSize="12" fill="#0f172a">
                  {node.title}
                </text>
              </g>
            ))}
          </svg>
        </div>
      </CardContent>
    </Card>
  );
}
