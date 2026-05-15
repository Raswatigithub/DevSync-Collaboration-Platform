# DevSync

Real-time developer collaboration platform built with React, Spring Boot, PostgreSQL, WebSockets, Docker, and optional OpenAI integration.

## Run Locally

```bash
docker compose -f infra/docker-compose.yml up --build
```

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Health: http://localhost:8080/actuator/health

## Features

- JWT authentication
- User profiles with bio and skills
- Project rooms with owner/member roles
- Project chat over WebSocket/STOMP
- Monaco-powered collaborative editor shell
- Code review workflow with line comments
- Forum questions, answers, tags, and votes
- Dashboard activity summary
- AI code assistant endpoint using OpenAI-compatible Chat Completions API

## Local Development

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Set these environment variables for local backend runs:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/devsync
SPRING_DATASOURCE_USERNAME=devsync
SPRING_DATASOURCE_PASSWORD=devsync
JWT_SECRET=replace-with-a-long-random-secret
OPENAI_API_KEY=optional
```
