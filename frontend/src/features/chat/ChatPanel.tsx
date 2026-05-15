import { useQuery } from "@tanstack/react-query";
import { Button, Input, List } from "antd";
import { Send } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { api } from "../../api/client";
import { createStompClient } from "../../api/socket";

type ChatMessage = {
  id: string;
  senderName: string;
  content: string;
  createdAt: string;
};

export function ChatPanel({ projectId }: { projectId: string }) {
  const [content, setContent] = useState("");
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const token = localStorage.getItem("accessToken") ?? "";
  const client = useMemo(() => createStompClient(token), [token]);

  const history = useQuery({
    queryKey: ["chat", projectId],
    queryFn: () => api.get<ChatMessage[]>(`/projects/${projectId}/chat`).then((res) => res.data),
  });

  useEffect(() => {
    setMessages([...(history.data ?? [])].reverse());
  }, [history.data]);

  useEffect(() => {
    client.onConnect = () => {
      client.subscribe(`/topic/projects/${projectId}/chat`, (frame) => {
        setMessages((current) => [...current, JSON.parse(frame.body)]);
      });
    };
    client.activate();
    return () => {
      void client.deactivate();
    };
  }, [client, projectId]);

  const send = () => {
    if (!content.trim() || !client.connected) return;
    client.publish({ destination: "/app/chat.send", body: JSON.stringify({ projectId, content }) });
    setContent("");
  };

  return (
    <div className="panel grid h-[620px] grid-rows-[1fr_auto]">
      <List
        className="overflow-auto p-4"
        dataSource={messages}
        renderItem={(message) => (
          <List.Item>
            <List.Item.Meta title={message.senderName} description={message.content} />
          </List.Item>
        )}
      />
      <div className="flex gap-2 border-t border-slate-200 p-3">
        <Input value={content} onChange={(event) => setContent(event.target.value)} onPressEnter={send} />
        <Button type="primary" icon={<Send size={16} />} onClick={send} />
      </div>
    </div>
  );
}
