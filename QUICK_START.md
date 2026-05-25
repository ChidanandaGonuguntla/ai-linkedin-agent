# Quick Start

## 1. Start dependencies

```bash
docker compose up -d postgres ollama
```

## 2. Pull Ollama model

```bash
docker exec -it ai-linkedin-agent-ollama ollama pull llama3.2:3b
```

## 3. Run the app

Option A: with local Maven:

```bash
mvn spring-boot:run
```

Option B: with Docker Compose:

```bash
docker compose up --build app
```

## 4. Trigger agent

```bash
curl -X POST http://localhost:8089/api/agent/run \
  -H "X-API-TOKEN: change-me-local-token"
```

## 5. Review drafts

```bash
curl http://localhost:8089/api/drafts/pending \
  -H "X-API-TOKEN: change-me-local-token"
```

## 6. Approve, edit, publish

Approve:

```bash
curl -X POST "http://localhost:8089/api/drafts/1/approve?approvedBy=chidha2019" \
  -H "X-API-TOKEN: change-me-local-token"
```

Edit before approval/publishing:

```bash
curl -X PUT http://localhost:8089/api/drafts/1/body \
  -H "X-API-TOKEN: change-me-local-token" \
  -H "Content-Type: application/json" \
  -d '{"body":"Updated LinkedIn post body here"}'
```

Publish approved draft:

```bash
curl -X POST http://localhost:8089/api/drafts/1/publish \
  -H "X-API-TOKEN: change-me-local-token"
```

Publishing requires LinkedIn OAuth/token setup. Without LinkedIn token, draft generation and approval still work.
