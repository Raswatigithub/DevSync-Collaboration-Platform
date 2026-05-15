import { useMutation, useQuery } from "@tanstack/react-query";
import { Button, Form, Input, List, Select, Tag, message } from "antd";
import { Link } from "react-router-dom";
import { api } from "../../api/client";

type Question = {
  id: string;
  title: string;
  body: string;
  tags: string[];
  authorName: string;
};

export function ForumPage() {
  const questions = useQuery({
    queryKey: ["questions"],
    queryFn: () => api.get<{ content: Question[] }>("/questions").then((res) => res.data.content),
  });
  const create = useMutation({
    mutationFn: (values: { title: string; body: string; tags?: string[] }) => api.post("/questions", values),
    onSuccess: () => {
      questions.refetch();
      message.success("Question posted");
    },
  });

  return (
    <div className="grid gap-4 xl:grid-cols-[380px_1fr]">
      <Form layout="vertical" className="panel p-4" onFinish={create.mutate}>
        <h1 className="mb-3 text-xl font-semibold">Ask a question</h1>
        <Form.Item name="title" label="Title" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="body" label="Details" rules={[{ required: true }]}>
          <Input.TextArea rows={5} />
        </Form.Item>
        <Form.Item name="tags" label="Tags">
          <Select mode="tags" placeholder="react, java, postgres" />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={create.isPending}>Post</Button>
      </Form>
      <List
        className="panel"
        loading={questions.isLoading}
        dataSource={questions.data ?? []}
        renderItem={(question) => (
          <List.Item actions={[<Link to={`/forum/questions/${question.id}`} key="open">Open</Link>]}>
            <List.Item.Meta
              title={question.title}
              description={<div><div className="mb-2">{question.body}</div>{question.tags.map((tag) => <Tag key={tag}>{tag}</Tag>)}</div>}
            />
          </List.Item>
        )}
      />
    </div>
  );
}
