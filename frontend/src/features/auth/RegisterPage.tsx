import { useMutation } from "@tanstack/react-query";
import { Link, useNavigate } from "react-router-dom";
import { message } from "antd";
import { api } from "../../api/client";
import { useAuthStore } from "../../store/authStore";
import { AuthForm } from "./AuthForm";

export function RegisterPage() {
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);
  const mutation = useMutation({
    mutationFn: (values: Record<string, string>) => api.post("/auth/register", values).then((res) => res.data),
    onSuccess: (data) => {
      setAuth(data.accessToken, data.user);
      navigate("/dashboard");
    },
    onError: () => message.error("Registration failed"),
  });

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-100 px-4">
      <div>
        <AuthForm mode="register" onSubmit={mutation.mutate} loading={mutation.isPending} />
        <Link className="mt-4 block text-center text-mint" to="/login">Already have an account?</Link>
      </div>
    </main>
  );
}
