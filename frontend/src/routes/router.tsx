import { createBrowserRouter, Navigate } from "react-router-dom";
import { AppLayout } from "../layouts/AppLayout";
import { LoginPage } from "../features/auth/LoginPage";
import { RegisterPage } from "../features/auth/RegisterPage";
import { DashboardPage } from "../features/dashboard/DashboardPage";
import { ProjectsPage } from "../features/projects/ProjectsPage";
import { ProjectWorkspace } from "../features/projects/ProjectWorkspace";
import { ForumPage } from "../features/forum/ForumPage";
import { QuestionPage } from "../features/forum/QuestionPage";
import { ProfilePage } from "../features/auth/ProfilePage";
import { useAuthStore } from "../store/authStore";

const Protected = ({ children }: { children: JSX.Element }) => {
  const token = useAuthStore((state) => state.token);
  return token ? children : <Navigate to="/login" replace />;
};

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  { path: "/register", element: <RegisterPage /> },
  {
    path: "/",
    element: (
      <Protected>
        <AppLayout />
      </Protected>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <DashboardPage /> },
      { path: "projects", element: <ProjectsPage /> },
      { path: "projects/:projectId", element: <ProjectWorkspace /> },
      { path: "forum", element: <ForumPage /> },
      { path: "forum/questions/:questionId", element: <QuestionPage /> },
      { path: "profile", element: <ProfilePage /> },
    ],
  },
]);
