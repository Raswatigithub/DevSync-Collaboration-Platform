import { Button, Layout, Menu } from "antd";
import { Code2, LayoutDashboard, LogOut, MessageSquareCode, User } from "lucide-react";
import { Link, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

const { Header, Sider, Content } = Layout;

export function AppLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const logout = useAuthStore((state) => state.logout);

  return (
    <Layout className="min-h-screen">
      <Sider width={236} theme="light" className="border-r border-slate-200">
        <div className="px-5 py-4 text-xl font-bold text-ink">DevSync</div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname.split("/")[1] || "dashboard"]}
          items={[
            { key: "dashboard", icon: <LayoutDashboard size={18} />, label: <Link to="/dashboard">Dashboard</Link> },
            { key: "projects", icon: <Code2 size={18} />, label: <Link to="/projects">Projects</Link> },
            { key: "forum", icon: <MessageSquareCode size={18} />, label: <Link to="/forum">Forum</Link> },
            { key: "profile", icon: <User size={18} />, label: <Link to="/profile">Profile</Link> },
          ]}
        />
      </Sider>
      <Layout>
        <Header className="flex items-center justify-end border-b border-slate-200 bg-white px-6">
          <Button
            icon={<LogOut size={16} />}
            onClick={() => {
              logout();
              navigate("/login");
            }}
          />
        </Header>
        <Content className="p-6">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
