# Backend Seed Data

This folder contains seed data and scripts to populate empty `auth` and `articles` databases through backend APIs, so article embeddings are generated in the vector store.

## Files

- `data/users.seed.json` - 15 users with shared password `Test100#`
- `data/articles.seed.json` - 150 medium-size programming articles with `authorId` UUID references
- `generate-seed-data.ps1` - regenerates both JSON files
- `seed-via-backend.ps1` - sends JSON data to backend seed endpoints

## Prerequisites

1. `auth`, `articles`, and `llm` services are running.
2. Environment variable `SEED_TOKEN` is the same for `auth` and `articles`.
   - Default value from properties is `seed-local-token`.

## Generate JSON

```powershell
powershell -ExecutionPolicy Bypass -File seed\generate-seed-data.ps1 -UsersCount 15 -ArticlesCount 150
```

## Run Seed Through Backend

```powershell
powershell -ExecutionPolicy Bypass -File seed\seed-via-backend.ps1 `
  -AuthBaseUrl http://localhost:8080 `
  -ArticlesBaseUrl http://localhost:7070 `
  -SeedToken seed-local-token
```

## Behavior

- User seed endpoint works only when `users` table is empty.
- Article seed endpoint works only when `article` table is empty.
- Articles are created through backend service logic and trigger embedding generation in `llm`.


```powershell
Invoke-WebRequest -Method Post `
  -Uri "http://localhost:7070/api/v1/articles/reindex-embeddings?batchSize=30&delayMs=3000" `
  -Headers @{ "X-Seed-Token" = "seed-local-token" }
```