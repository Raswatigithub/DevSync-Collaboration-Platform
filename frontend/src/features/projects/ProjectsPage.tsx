import { useMutation, useQuery } from "@tanstack/react-query";
import { Button, Form, Input, List, Modal, message } from "antd";
import { Plus } from "lucide-react";
import { useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../../api/client";
import type { Project } from "./types";

export function ProjectsPage() {
  const [open, setOpen] = useState(false);
  const projects = useQuery({ queryKey: ["projects"], queryFn: () => api.get<Project[]>("/projects").then((res) => res.data) });
  const create = useMutation({
    mutationFn: (values: { name: string; description?: string }) => api.post("/projects", values),
    onSuccess: () => {
      setOpen(false);
      projects.refetch();
      message.success("Project created");
    },
  });

  return (
    <section>
      <div className="mb-5 flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Projects</h1>
        <Button type="primary" icon={<Plus size={16} />} onClick={() => setOpen(true)}>New project</Button>
      </div>
      <List
        className="panel"
        loading={projects.isLoading}
        dataSource={projects.data ?? []}
        renderItem={(project) => (
          <List.Item actions={[<Link to={`/projects/${project.id}`} key="open">Open</Link>]}>
            <List.Item.Meta title={project.name} description={project.description || "No description"} />
            <span className="text-xs font-semibold text-mint">{project.role}</span>
          </List.Item>
        )}
      />
      <Modal title="Create project" open={open} onCancel={() => setOpen(false)} footer={null}>
        <Form layout="vertical" onFinish={create.mutate}>
          <Form.Item name="name" label="Name" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Button type="primary" htmlType="submit" loading={create.isPending}>Create</Button>
        </Form>
      </Modal>
    </section>
  );
}
