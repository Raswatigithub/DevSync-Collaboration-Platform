import { create } from "zustand";

export type User = {
  id: string;
  username: string;
  email: string;
  bio?: string;
  skills: string[];
};

type AuthState = {
  user?: User;
  token?: string;
  setAuth: (token: string, user: User) => void;
  logout: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem("accessToken") ?? undefined,
  user: undefined,
  setAuth: (token, user) => {
    localStorage.setItem("accessToken", token);
    set({ token, user });
  },
  logout: () => {
    localStorage.removeItem("accessToken");
    set({ token: undefined, user: undefined });
  },
}));
