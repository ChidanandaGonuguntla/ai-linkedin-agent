# AI LinkedIn Agent

Approval-first enterprise Spring Boot agent that crawls trusted AI sources, ranks fresh updates, generates LinkedIn drafts, and publishes only after approval.

## What this app does

1. Crawls RSS feeds from trusted AI sources.
2. Extracts readable article content.
3. Deduplicates by URL and content hash.
4. Scores articles by freshness, relevance, and source credibility.
5. Generates summaries and LinkedIn post drafts using Ollama or OpenAI.
6. Stores drafts as `PENDING_APPROVAL`.
7. Lets you approve/reject drafts.
8. Publishes to LinkedIn only when an approved draft is explicitly published.

## Run locally

```bash
docker compose up -d postgres ollama
# Pull model once if using Ollama
# docker exec -it ai-linkedin-agent-ollama ollama pull llama3.2:3b
./mvnw spring-boot:run
```

If you do not have Maven wrapper, run:

```bash
mvn spring-boot:run
```

## API auth

All `/api/**` endpoints require header:

```bash
X-API-TOKEN: change-me-local-token
```

Override with:

```bash
APP_API_TOKEN=your-secret-token
```

## Key APIs

Trigger full agent manually:

```bash
curl -X POST http://localhost:8089/api/agent/run \
  -H 'X-API-TOKEN: change-me-local-token'
```

View pending drafts:

```bash
curl http://localhost:8089/api/drafts/pending \
  -H 'X-API-TOKEN: change-me-local-token'
```

Approve a draft:

```bash
curl -X POST http://localhost:8089/api/drafts/1/approve \
  -H 'X-API-TOKEN: change-me-local-token'
```

Reject a draft:

```bash
curl -X POST http://localhost:8089/api/drafts/1/reject \
  -H 'X-API-TOKEN: change-me-local-token'
```

Publish an approved draft:

```bash
curl -X POST http://localhost:8089/api/drafts/1/publish \
  -H 'X-API-TOKEN: change-me-local-token'
```

## LinkedIn notes

LinkedIn posting requires a LinkedIn Developer app, OAuth, and the proper posting permission such as `w_member_social`. Keep this in approval-first mode.

## Deployment recommendation

Best open-source deployment path:

- VPS: Hetzner, DigitalOcean, Linode, etc.
- PaaS: Coolify, CapRover, or Dokku
- Runtime: Docker Compose
- Database: PostgreSQL
- LLM: Ollama on a larger VPS, or OpenAI/Groq/Gemini API for lighter hosting
