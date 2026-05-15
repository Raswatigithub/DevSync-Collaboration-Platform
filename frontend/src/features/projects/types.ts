export type Project = {
  id: string;
  name: string;
  description?: string;
  ownerId: string;
  role: "OWNER" | "MEMBER";
};

export type CodeFile = {
  id: string;
  projectId: string;
  path: string;
  language?: string;
  content?: string;
  updatedAt: string;
};
