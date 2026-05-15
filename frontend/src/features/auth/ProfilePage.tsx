import { useMutation, useQuery } from "@tanstack/react-query";
import { Button, Form, Input, Select, message } from "antd";
import { api } from "../../api/client";

export function ProfilePage() {
  const profile = useQuery({ queryKey: ["me"], queryFn: () => api.get("/users/me").then((res) => res.data) });
  const update = useMutation({
    mutationFn: (values: { bio?: string; skills?: string[] }) => api.patch("/users/me", values).then((res) => res.data),
    onSuccess: () => {
      profile.refetch();
      message.success("Profile updated");
    },
  });

  return (
    <section className="panel max-w-2xl p-5">
      <h1 className="mb-4 text-xl font-semibold">Profile</h1>
      <Form layout="vertical" initialValues={profile.data} onFinish={update.mutate}>
        <Form.Item name="bio" label="Bio">
          <Input.TextArea rows={4} />
        </Form.Item>
        <Form.Item name="skills" label="Skills">
          <Select mode="tags" placeholder="React, Java, PostgreSQL" />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={update.isPending}>Save</Button>
      </Form>
    </section>
  );
}
