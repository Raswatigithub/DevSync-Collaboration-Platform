import { useMutation, useQuery } from "@tanstack/react-query";
import { Button, Form, Input, List, Select, Tag, message } from "antd";
import { api } from "../../api/client";

type Review = {
  id: string;
  title: string;
  status: "OPEN" | "APPROVED" | "CHANGES_REQUESTED";
  comments: { id: string; filePath: string; lineNumber: number; content: string }[];
};

export function ReviewPanel({ projectId }: { projectId: string }) {
  const reviews = useQuery({ queryKey: ["reviews", projectId], queryFn: () => api.get<Review[]>(`/projects/${projectId}/reviews`).then((res) => res.data) });
  const create = useMutation({
    mutationFn: (values: { title: string }) => api.post(`/projects/${projectId}/reviews`, values),
    onSuccess: () => {
      reviews.refetch();
      message.success("Review created");
    },
  });
  const comment = useMutation({
    mutationFn: ({ reviewId, ...values }: { reviewId: string; filePath: string; lineNumber: number; content: string }) =>
      api.post(`/reviews/${reviewId}/comments`, values),
    onSuccess: () => reviews.refetch(),
  });
  const status = useMutation({
    mutationFn: ({ reviewId, next }: { reviewId: string; next: string }) => api.patch(`/reviews/${reviewId}/status`, { status: next }),
    onSuccess: () => reviews.refetch(),
  });

  return (
    <div className="grid gap-4 lg:grid-cols-[340px_1fr]">
      <Form layout="vertical" className="panel p-4" onFinish={create.mutate}>
        <h2 className="mb-3 text-lg font-semibold">New review</h2>
        <Form.Item name="title" label="Title" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={create.isPending}>Create</Button>
      </Form>
      <List
        className="panel"
        loading={reviews.isLoading}
        dataSource={reviews.data ?? []}
        renderItem={(review) => (
          <List.Item>
            <div className="w-full">
              <div className="mb-3 flex items-center justify-between">
                <div className="font-semibold">{review.title}</div>
                <div className="flex items-center gap-2">
                  <Tag color={review.status === "APPROVED" ? "green" : review.status === "CHANGES_REQUESTED" ? "red" : "blue"}>{review.status}</Tag>
                  <Select
                    size="small"
                    value={review.status}
                    options={["OPEN", "APPROVED", "CHANGES_REQUESTED"].map((item) => ({ value: item, label: item }))}
                    onChange={(next) => status.mutate({ reviewId: review.id, next })}
                  />
                </div>
              </div>
              <List
                size="small"
                dataSource={review.comments}
                renderItem={(item) => <List.Item>{item.filePath}:{item.lineNumber} - {item.content}</List.Item>}
              />
              <Form
                className="mt-3 grid gap-2 md:grid-cols-[1fr_100px_1fr_auto]"
                onFinish={(values) => comment.mutate({ reviewId: review.id, ...values, lineNumber: Number(values.lineNumber) })}
              >
                <Form.Item name="filePath" rules={[{ required: true }]} className="mb-0">
                  <Input placeholder="src/Main.java" />
                </Form.Item>
                <Form.Item name="lineNumber" rules={[{ required: true }]} className="mb-0">
                  <Input placeholder="Line" />
                </Form.Item>
                <Form.Item name="content" rules={[{ required: true }]} className="mb-0">
                  <Input placeholder="Comment" />
                </Form.Item>
                <Button htmlType="submit">Add</Button>
              </Form>
            </div>
          </List.Item>
        )}
      />
    </div>
  );
}
