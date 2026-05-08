param(
    [int]$UsersCount = 15,
    [int]$ArticlesCount = 150
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$dataDir = Join-Path $PSScriptRoot "data"
New-Item -ItemType Directory -Force -Path $dataDir | Out-Null

$password = "Test100#"

$adjectives = @("atomic","binary","cobalt","delta","eager","fast","global","hidden","iterative","kinetic","logical","modern","neural","optic","parallel","quantum","robust","silent","turbo","unified","vector","wired","young","zen")
$nouns = @("coder","engineer","builder","architect","analyst","mentor","developer","designer","maintainer","reviewer","tester","writer")

$topics = @(
    "Advanced Git Branching Strategies","Designing REST APIs for Scale","Practical Event-Driven Architecture","PostgreSQL Indexing for Developers","Building Reliable Background Jobs",
    "Caching Patterns in Microservices","Domain-Driven Design in Practice","Message Queues and Delivery Guarantees","Observability with Logs Metrics Traces","Testing Strategy for Backend Systems",
    "JWT Authentication Deep Dive","Secure File Upload Pipelines","Feature Flags Without Technical Debt","Refactoring Legacy Java Services","Semantic Search with Vector Databases",
    "Rate Limiting and Abuse Protection","Clean Architecture in Spring Boot","API Versioning and Compatibility","Fault Tolerance with Retry and Backoff","Database Migrations in Production"
)

$openers = @(
    "Most production incidents are caused by small design shortcuts that compound over time.",
    "A reliable backend is usually the result of explicit tradeoffs, not accidental correctness.",
    "Teams move faster when architecture decisions are visible and testable.",
    "The hard part is rarely writing code; it is keeping behavior stable while requirements change."
)
$focuses = @("contract stability and compatibility","latency under peak traffic","error handling and retries","data consistency and idempotency","operability and incident response")
$patterns = @("outbox pattern","saga orchestration","request coalescing","bulkhead isolation","circuit breaker","write-through caching","append-only event log","blue-green deployment")
$pitfalls = @("hidden coupling through shared tables","timeouts without retry policy","schema changes without backfill plan","missing idempotency keys on write APIs","non-deterministic integration tests","noisy logs without correlation ids")

$codeSamples = @(
    "Code idea: @Transactional handler with processed-event deduplication.",
    "Code idea: retry wrapper for remote calls with bounded backoff.",
    "Code idea: composite index on (author_id, created_at desc) for feed queries.",
    "Code idea: structured ApiError payload with correlation id.",
    "Code idea: expose health and metrics endpoints for monitoring."
)

$users = @()
for ($i = 1; $i -le $UsersCount; $i++) {
    $adj = $adjectives | Get-Random
    $noun = $nouns | Get-Random
    $username = "{0}_{1}_{2:d2}" -f $adj, $noun, $i
    $nameAdj = $adj.Substring(0,1).ToUpper() + $adj.Substring(1)
    $nameNoun = $noun.Substring(0,1).ToUpper() + $noun.Substring(1)
    $users += [ordered]@{
        id = [guid]::NewGuid().ToString()
        username = $username
        email = "$username@example.com"
        name = "$nameAdj $nameNoun"
        password = $password
    }
}

$articles = @()
for ($i = 1; $i -le $ArticlesCount; $i++) {
    $author = $users | Get-Random
    $topic = $topics[($i - 1) % $topics.Count]
    $title = "$topic - Guide $i"
    $template = $i % 4

    $p1 = "$($openers[($i + 1) % $openers.Count]) For $title, prioritize $($focuses[($i + 2) % $focuses.Count]) and avoid $($pitfalls[($i + 3) % $pitfalls.Count])."
    $p2 = "Use explicit boundaries and a stable contract model. Apply $($patterns[($i + 1) % $patterns.Count]) only if it reduces coordination cost and improves resilience."
    $p3 = "Implementation should preserve idempotency, deterministic retries, and traceability through correlation ids. Keep write paths observable and safe for rollback."
    $p4 = "Validation should include unit, repository, and narrow end-to-end checks. Add latency and error-rate monitoring before broad rollout."
    $p5 = "Operationally, release in phases and define rollback criteria in advance. This keeps iteration speed high while keeping reliability predictable."
    $code = $codeSamples[$i % $codeSamples.Count]

    if ($template -eq 0) {
        $markdown = @"
# $title

## Problem Framing
$p1

## Architecture Decision
$p2

## Implementation Walkthrough
$p3

$code

## Validation and Metrics
$p4

## Migration Plan
$p5
"@
    }
    elseif ($template -eq 1) {
        $markdown = @"
# $title

## Why Teams Struggle
$p1

## A Better Baseline
$p2

## Step-by-Step Refactor
$p3

## Example Snippet
$code

## Rollout Checklist
$p4

## Incident Readiness
$p5
"@
    }
    elseif ($template -eq 2) {
        $markdown = @"
# $title

## Constraints
$p1

## Data Model and Contracts
$p2

## Failure Modes
$p3

## Reference Implementation
$code

## Testing Strategy
$p4

## Cost and Capacity Notes
$p5
"@
    }
    else {
        $markdown = @"
# $title

## Context and Tradeoffs
$p1

## Design Alternatives
$p2

## Recommended Approach
$p3

## Minimal Viable Implementation
$code

## Monitoring and Alerting
$p4

## Common Anti-Patterns
$p5
"@
    }

    $articles += [ordered]@{
        authorId = $author.id
        title = $title
        imageUrl = "https://picsum.photos/seed/prog_article_$i/1200/630"
        published = $true
        markdownContent = $markdown
    }
}

$users | ConvertTo-Json -Depth 4 | Set-Content -Path (Join-Path $dataDir "users.seed.json") -Encoding UTF8
$articles | ConvertTo-Json -Depth 4 | Set-Content -Path (Join-Path $dataDir "articles.seed.json") -Encoding UTF8

Write-Output "Generated users: $UsersCount"
Write-Output "Generated articles: $ArticlesCount"
