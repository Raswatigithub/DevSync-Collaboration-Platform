import { useQuery } from "@tanstack/react-query";
import { Card, Col, Row, Skeleton } from "antd";
import { api } from "../../api/client";

export function DashboardPage() {
  const { data, isLoading } = useQuery({
    queryKey: ["dashboard"],
    queryFn: () => api.get("/dashboard/summary").then((res) => res.data),
  });

  if (isLoading) return <Skeleton active />;

  const cards = [
    ["Projects joined", data?.projectsJoined ?? 0],
    ["Questions posted", data?.questionsPosted ?? 0],
    ["Answers posted", data?.answersPosted ?? 0],
    ["Reviews created", data?.reviewsCreated ?? 0],
  ];

  return (
    <section>
      <h1 className="mb-5 text-2xl font-semibold">Dashboard</h1>
      <Row gutter={[16, 16]}>
        {cards.map(([label, value]) => (
          <Col xs={24} md={12} xl={6} key={label}>
            <Card>
              <div className="text-sm text-slate-500">{label}</div>
              <div className="mt-2 text-3xl font-semibold text-ink">{value}</div>
            </Card>
          </Col>
        ))}
      </Row>
    </section>
  );
}
