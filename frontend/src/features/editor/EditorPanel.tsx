import Editor from "@monaco-editor/react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { Button, Input, Select, Splitter, message } from "antd";
import { Save } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { api } from "../../api/client";
import { createStompClient } from "../../api/socket";
import type { CodeFile } from "../projects/types";
import { AiPanel } from "../ai/AiPanel";

export function EditorPanel({ projectId }: { projectId: string }) {
  const [activeFile, setActiveFile] = useState<CodeFile>();
  const [content, setContent] = useState("// Start collaborating in DevSync\n");
  const token = localStorage.getItem("accessToken") ?? "";
  const client = useMemo(() => createStompClient(token), [token]);
  const files = useQuery({ queryKey: ["files", projectId], queryFn: () => api.get<CodeFile[]>(`/projects/${projectId}/files`).then((res) => res.data) });
  const createFile = useMutation({
    mutationFn: () => api.post<CodeFile>(`/projects/${projectId}/files`, { path: "src/Main.java", language: "java", content }).then((res) => res.data),
    onSuccess: (file) => {
      setActiveFile(file);
      files.refetch();
    },
  });
  const save = useMutation({
    mutationFn: () => api.patch(`/files/${activeFile?.id}`, { path: activeFile?.path, language: activeFile?.language, content }),
    onSuccess: () => message.success("Snapshot saved"),
  });

  useEffect(() => {
    if (!activeFile && files.data?.length) {
      setActiveFile(files.data[0]);
      setContent(files.data[0].content ?? "");
    }
  }, [activeFile, files.data]);

  useEffect(() => {
    client.onConnect = () => {
      client.subscribe(`/topic/projects/${projectId}/editor`, (frame) => {
        const event = JSON.parse(frame.body);
        if (event.fileId === activeFile?.id) setContent(event.content);
      });
    };
    client.activate();
    return () => {
      void client.deactivate();
    };
  }, [client, projectId, activeFile?.id]);

  const publish = (value?: string) => {
    const next = value ?? "";
    setContent(next);
    if (client.connected && activeFile) {
      client.publish({
        destination: "/app/editor.change",
        body: JSON.stringify({ projectId, fileId: activeFile.id, content: next, version: Date.now() }),
      });
    }
  };

  return (
    <Splitter className="panel h-[680px]">
      <Splitter.Panel defaultSize="74%" min="50%">
        <div className="flex items-center gap-2 border-b border-slate-200 p-3">
          <Select
            className="min-w-56"
            placeholder="Select file"
            value={activeFile?.id}
            options={(files.data ?? []).map((file) => ({ value: file.id, label: file.path }))}
            onChange={(id) => {
              const file = files.data?.find((item) => item.id === id);
              setActiveFile(file);
              setContent(file?.content ?? "");
            }}
          />
          <Button onClick={() => createFile.mutate()}>Create starter file</Button>
          <Button type="primary" icon={<Save size={16} />} disabled={!activeFile} onClick={() => save.mutate()} />
        </div>
        <Editor height="620px" language={activeFile?.language ?? "typescript"} value={content} onChange={publish} theme="vs-dark" />
      </Splitter.Panel>
      <Splitter.Panel min="280px">
        <AiPanel language={activeFile?.language ?? "text"} code={content} />
      </Splitter.Panel>
    </Splitter>
  );
}
