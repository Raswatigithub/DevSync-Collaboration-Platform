import { Tabs } from "antd";
import { useParams } from "react-router-dom";
import { ChatPanel } from "../chat/ChatPanel";
import { EditorPanel } from "../editor/EditorPanel";
import { ReviewPanel } from "../reviews/ReviewPanel";

export function ProjectWorkspace() {
  const { projectId } = useParams();
  if (!projectId) return null;

  return (
    <section>
      <h1 className="mb-4 text-2xl font-semibold">Project Workspace</h1>
      <Tabs
        items={[
          { key: "editor", label: "Editor", children: <EditorPanel projectId={projectId} /> },
          { key: "chat", label: "Chat", children: <ChatPanel projectId={projectId} /> },
          { key: "reviews", label: "Reviews", children: <ReviewPanel projectId={projectId} /> },
        ]}
      />
    </section>
  );
}
