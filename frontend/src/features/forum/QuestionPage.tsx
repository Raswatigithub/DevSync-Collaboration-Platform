import { useMutation, useQuery } from "@tanstack/react-query";
import { Button, Form, Input, List, Skeleton, Tag, message } from "antd";
import { useParams } from "react-router-dom";
import { api } from "../../api/client";

type Answer = {
  id: string;
  authorName: string;
  body: string;
};

type Question = {
  id: string;
  title: string;
  body: string;
  tags: string[];
  authorName: string;
  answers: Answer[];
};

export function QuestionPage() {
  const { questionId } = useParams();
  const question = useQuery({
    queryKey: ["question", questionId],
    queryFn: () => api.get<Question>(`/questions/${questionId}`).then((res) => res.data),
    enabled: Boolean(questionId),
  });
  const answer = useMutation({
    mutationFn: (values: { body: string }) => api.post(`/questions/${questionId}/answers`, values),
    onSuccess: () => {
      question.refetch();
      message.success("Answer posted");
    },
  });
  const vote = useMutation({
    mutationFn: (value: number) => api.post("/votes", { targetType: "QUESTION", targetId: questionId, value }),
    onSuccess: () => message.success("Vote saved"),
  });

  if (question.isLoading) return <Skeleton active />;
  if (!question.data) return null;

  return (
    <section className="grid gap-4">
      <article className="panel p-5">
        <div className="mb-3 flex items-start justify-between gap-4">
          <h1 className="text-2xl font-semibold">{question.data.title}</h1>
          <div className="flex gap-2">
            <Button onClick={() => vote.mutate(1)}>Upvote</Button>
            <Button onClick={() => vote.mutate(-1)}>Downvote</Button>
          </div>
        </div>
        <p className="whitespace-pre-wrap">{question.data.body}</p>
        <div className="mt-3">{question.data.tags.map((tag) => <Tag key={tag}>{tag}</Tag>)}</div>
      </article>
      <List
        className="panel"
        header={<div className="font-semibold">Answers</div>}
        dataSource={question.data.answers}
        renderItem={(item) => (
          <List.Item>
            <List.Item.Meta title={item.authorName} description={item.body} />
          </List.Item>
        )}
      />
      <Form layout="vertical" className="panel p-4" onFinish={answer.mutate}>
        <Form.Item name="body" label="Your answer" rules={[{ required: true }]}>
          <Input.TextArea rows={4} />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={answer.isPending}>Post answer</Button>
      </Form>
    </section>
  );
}
