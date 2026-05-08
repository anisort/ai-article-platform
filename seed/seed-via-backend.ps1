param(
    [string]$AuthBaseUrl = "http://localhost:8080",
    [string]$ArticlesBaseUrl = "http://localhost:7070",
    [string]$SeedToken = "seed-local-token"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$dataDir = Join-Path $PSScriptRoot "data"
$usersPath = Join-Path $dataDir "users.seed.json"
$articlesPath = Join-Path $dataDir "articles.seed.json"

if (!(Test-Path $usersPath) -or !(Test-Path $articlesPath)) {
    throw "Seed JSON files are missing. Run seed/generate-seed-data.ps1 first."
}

$users = Get-Content $usersPath -Raw | ConvertFrom-Json
$articles = Get-Content $articlesPath -Raw | ConvertFrom-Json

$seedHeaders = @{
    "Content-Type" = "application/json"
    "X-Seed-Token" = $SeedToken
}

$seedUsersUrl = "$AuthBaseUrl/api/v1/users/seed"
$createdUsers = Invoke-RestMethod -Method Post -Uri $seedUsersUrl -Headers $seedHeaders -Body ($users | ConvertTo-Json -Depth 6)

$seedArticlesUrl = "$ArticlesBaseUrl/api/v1/articles/seed"
$createdArticles = Invoke-RestMethod -Method Post -Uri $seedArticlesUrl -Headers $seedHeaders -Body ($articles | ConvertTo-Json -Depth 8)

Write-Output "Seed completed via backend."
Write-Output ("Users created: " + $createdUsers.Count)
Write-Output ("Articles created: " + $createdArticles.Count)
