import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const createStompClient = (token: string) =>
  new Client({
    webSocketFactory: () => new SockJS(`${import.meta.env.VITE_WS_URL ?? "http://localhost:8080"}/ws`),
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    reconnectDelay: 3000,
  });
