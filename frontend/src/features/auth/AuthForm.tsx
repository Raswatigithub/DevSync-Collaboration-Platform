import { Button, Form, Input } from "antd";

type Props = {
  mode: "login" | "register";
  onSubmit: (values: Record<string, string>) => void;
  loading?: boolean;
};

export function AuthForm({ mode, onSubmit, loading }: Props) {
  return (
    <Form layout="vertical" onFinish={onSubmit} className="w-full max-w-sm panel p-6">
      <h1 className="mb-6 text-2xl font-semibold">{mode === "login" ? "Sign in" : "Create account"}</h1>
      {mode === "register" && (
        <Form.Item name="username" label="Username" rules={[{ required: true, min: 3 }]}>
          <Input />
        </Form.Item>
      )}
      <Form.Item name="email" label="Email" rules={[{ required: true, type: "email" }]}>
        <Input />
      </Form.Item>
      <Form.Item name="password" label="Password" rules={[{ required: true, min: 8 }]}>
        <Input.Password />
      </Form.Item>
      <Button type="primary" htmlType="submit" loading={loading} block>
        {mode === "login" ? "Sign in" : "Register"}
      </Button>
    </Form>
  );
}
