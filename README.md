# AI Article Platform

**AI Article Platform** is a full-stack microservices application for discovering, creating, and managing articles with AI-assisted workflows.
It combines an **Angular** frontend with **Spring Boot** backend services (Auth, Articles, Files, and LLM), backed by **PostgreSQL**, **Redis**, and proxied through **Nginx** for local development via Docker Compose.

---

## Stack

**Frontend:** Angular (port `4200`; proxied as `/` via Nginx)

**Backend services (Spring Boot):**

* **Auth service** (`8080`; proxied as `/auth/`)
* **Articles service** (`7070`; proxied as `/api/articles/`)
* **Files service** (`9090`; proxied as `/api/files/`)
* **LLM service** (`8090`; proxied as `/api/llm/`)

**Database:**

* `pg-auth` (PostgreSQL 16)
* `pg-articles` (PostgreSQL 16)
* `pg-llm` (pgvector on PostgreSQL 16)

**Cache:** Redis 7.2 (`redis-cache`)

**Reverse proxy:** Nginx (`NGINX_HOST_PORT`, default `81`)

**Containerization:** Docker + Docker Compose

**Cloud/AI integrations:** Google Cloud (Vertex AI + GCS) and OAuth providers (Google/GitHub)

> `secret/credentials.json` must contain valid Google service account credentials for GCP-dependent features.

---

## Core Functionality

### User and Auth

* User registration and login flows
* OAuth2/OIDC integration with JWT issuer/JWK endpoints
* Profile management endpoints
* Google and GitHub OAuth client integration (via env configuration)

### Articles

* Create, read, update, and delete articles
* Search and recommendation endpoints
* Profile page/article-related API endpoints
* Media-related article operations

### AI / LLM

* AI assistant/chat endpoints for article workflows
* Article discovery and description generation endpoints
* Vector operations backed by pgvector
* Vertex AI embeddings + Gemini-backed functionality (via env configuration)

### Files

* File upload/storage API backed by Google Cloud Storage
* JWT-protected access and service-to-service usage

---

## Repository Structure

```text
auth/                 Spring Boot Auth service
articles/             Spring Boot Articles service
files/                Spring Boot Files service
llm/                  Spring Boot LLM service
frontend/             Angular application
dockerfiles/          Dockerfiles for all app services
nginx/                Nginx config (default.conf)
secret/               Local credentials mount (credentials.json)
ui-tests/             UI test scaffolding
docker-compose.yaml   Local orchestration
.env.example          Example environment variables
```

---

## Prerequisites

* Docker 20+ and Docker Compose v2
* (Optional) Java 21 + Maven (run services outside Docker)
* (Optional) Node.js 18+ (run frontend outside Docker)
* Google Cloud service account key JSON at `secret/credentials.json`

---

## Quick Start (Docker Compose)

1. Create `.env` from the template:

   ```bash
   cp .env.example .env
   ```

2. Update required values in `.env`:

   * `DB_USER`, `DB_PASSWORD`
   * `DB_AUTH_DATABASE`, `DB_ARTICLES_DATABASE`, `DB_LLM_DATABASE`
   * `DATASOURCE_AUTH_URL`, `DATASOURCE_ARTICLES_URL`, `DATASOURCE_LLM_URL`
   * `AUTH_*`, `RESOURCE_SERVER_*`, `FRONTEND_URL_HOST`
   * `REDIS_*`, `CACHE_TYPE`
   * `VERTEX_*`, `GCP_STORAGE_BUCKET_NAME`
   * OAuth provider credentials (`GOOGLE_OAUTH2_*`, `GITHUB_OAUTH2_*`) if used

3. Ensure credentials file exists:

   ```text
   secret/credentials.json
   ```

4. Build and start all services:

   ```bash
   docker compose up -d --build
   ```

5. Open the app:

   * Nginx entrypoint: `http://localhost:81`
   * Frontend direct: `http://localhost:4200`
   * Auth direct: `http://localhost:8080`
   * Articles direct: `http://localhost:7070`
   * Files direct: `http://localhost:9090`
   * LLM direct: `http://localhost:8090`

6. Stop the stack:

   ```bash
   docker compose down
   ```

To also remove volumes:

```bash
docker compose down -v
```

---

## Routing Through Nginx

Configured in `nginx/default.conf`:

* `/` -> `frontend:4200`
* `/auth/` -> `auth:8080`
* `/api/articles/` -> `articles:7070/api/`
* `/api/files/` -> `files:9090/api/`
* `/api/llm/` -> `llm:8090/api/`
* `/oauth2/`, `/login`, `/logout` are forwarded to Auth with proxy headers

---

## Environment Variables

See `.env.example` for the complete list.

### Databases

* `DB_USER`, `DB_PASSWORD`
* `DB_AUTH_DATABASE`, `DB_ARTICLES_DATABASE`, `DB_LLM_DATABASE`
* `DATASOURCE_AUTH_URL`, `DATASOURCE_ARTICLES_URL`, `DATASOURCE_LLM_URL`

### JPA / Hibernate

* `SPRING_JPA_HIBERNATE_DDL_AUTO`
* `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT`

### Security / OAuth / JWT

* `AUTH_CLIENT_ID`, `AUTH_CLIENT_SECRET`, `AUTH_CLIENT_REDIRECT_URI`, `AUTH_ISSUER_URI`
* `RESOURCE_SERVER_ISSUER_URI`, `RESOURCE_SERVER_JWK_SET_URI`
* `GOOGLE_OAUTH2_CLIENT_ID`, `GOOGLE_OAUTH2_CLIENT_SECRET`
* `GITHUB_OAUTH2_CLIENT_ID`, `GITHUB_OAUTH2_CLIENT_SECRET`

### URLs / Service Discovery

* `FRONTEND_URL_HOST`, `NGINX_HOST_PORT`
* `AUTH_SERVICE_URL_INTERNAL`, `FILES_SERVICE_URL_INTERNAL`, `LLM_SERVICE_URL_INTERNAL`
* `FILES_API_URL_INTERNAL`

### Redis Cache

* `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
* `CACHE_TYPE` (use `redis`)

### Vector / AI / GCP

* `INDEX_TYPE`, `DISTANCE_TYPE`, `INITIALIZE_SCHEMA`
* `VERTEX_PROJECT_ID`, `VERTEX_LOCATION`, `VERTEX_EMBEDDING_MODEL`
* `GCP_STORAGE_BUCKET_NAME`
* `GOOGLE_APPLICATION_CREDENTIALS`

---

## Data Persistence (Docker Volumes)

* `pg-data-auth`
* `pg-data-articles`
* `pg-data-llm`
* `redis-data`

---

## Local Development Tips

* Rebuild after env or Dockerfile changes:

  ```bash
  docker compose up -d --build
  ```

* Apply `.env` changes cleanly:

  ```bash
  docker compose down
  docker compose up -d
  ```

* Check logs:

  ```bash
  docker compose logs -f
  ```

---

## Troubleshooting

* **GCP errors / startup failures:** verify `secret/credentials.json`, `GOOGLE_APPLICATION_CREDENTIALS`, `VERTEX_*`, and bucket variables.
* **JWT/OAuth validation failures:** confirm `AUTH_ISSUER_URI`, `RESOURCE_SERVER_ISSUER_URI`, and `RESOURCE_SERVER_JWK_SET_URI`.
* **DB connectivity issues:** ensure datasource URLs use Compose service names (`pg-auth`, `pg-articles`, `pg-llm`).
* **Redis auth errors:** validate `REDIS_PASSWORD` and `CACHE_TYPE=redis`.
* **Port conflicts:** check ports `81`, `4200`, `7070`, `8080`, `8090`, `9090`, `5438`, `5439`, `5440`, and `6379`.

