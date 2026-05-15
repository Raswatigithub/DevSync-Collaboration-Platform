import { useMutation } from "@tanstack/react-query";
import { Button, Radio, Typography } from "antd";
import { Bot } from "lucide-react";
import { useState } from "react";
import { api } from "../../api/client";

export function AiPanel({ language, code }: { language: string; code: string }) {
  const [task, setTask] = useState("Suggest improvements");
  const ai = useMutation({
    mutationFn: () => api.post("/ai/code-suggestion", { language, code, task }).then((res) => res.data),
  });

  return (
    <aside className="h-full overflow-auto p-4">
      <div className="mb-3 flex items-center gap-2 text-lg font-semibold"><Bot size={18} /> AI Assistant</div>
      <Radio.Group
        className="mb-4 grid gap-2"
        value={task}
        onChange={(event) => setTask(event.target.value)}
        options={["Suggest improvements", "Fix bugs", "Explain code", "Optimize performance"]}
      />
      <Button type="primary" block onClick={() => ai.mutate()} loading={ai.isPending}>Run</Button>
      <Typography.Paragraph className="mt-4 whitespace-pre-wrap text-sm">
        {ai.data?.result ?? "Select an action to review the current editor content."}
      </Typography.Paragraph>
    </aside>
  );
}
