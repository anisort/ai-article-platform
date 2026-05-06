# Entity Model Across Microservices

## Articles Service (`articles`)

### `Article` (`article`)
| Attribute | Type |
|---|---|
| `id` | `UUID` |
| `authorId` | `UUID` |
| `title` | `String` |
| `imageUrl` | `String` |
| `published` | `boolean` |
| `summary` | `String` |
| `markdownContent` | `String` (`TEXT`) |
| `createdAt` | `ZonedDateTime` |

### `ArticleLike` (`article_like`)
| Attribute | Type |
|---|---|
| `id` | `UUID` |
| `userId` | `UUID` |
| `article` | `Article` (`ManyToOne`) |

Unique constraint: `(user_id, article_id)`.

### `ArticleImage` (`article_image`)
| Attribute | Type |
|---|---|
| `id` | `UUID` |
| `articleId` | `UUID` |
| `imageUrl` | `String` (`TEXT`) |

## Auth Service (`auth`)

### `CustomUserDetails` (`users`)
| Attribute | Type |
|---|---|
| `id` | `UUID` |
| `username` | `String` |
| `email` | `String` |
| `password` | `String` |
| `role` | `Role` (`ADMIN`, `USER`) |
| `avatar` | `String` |
| `googleSub` | `String` |
| `githubSub` | `String` |
| `name` | `String` |

### `UserFollow` (`user_follow`)
| Attribute | Type |
|---|---|
| `id` | `UUID` |
| `followerId` | `UUID` |
| `followingId` | `UUID` |

Unique constraint: `(follower_id, following_id)`.

## LLM Service (`llm`)

### `ArticleAssistantChat` (`article_assistant_chat`)
| Attribute | Type |
|---|---|
| `id` | `UUID` |
| `articleId` | `UUID` |
| `title` | `String` |
| `createdAt` | `ZonedDateTime` |

### `ArticleAssistantMessage` (`article_assistant_message`)
| Attribute | Type |
|---|---|
| `id` | `UUID` |
| `chat` | `ArticleAssistantChat` (`ManyToOne`) |
| `role` | `AssistantMessageRole` (`USER`, `ASSISTANT`) |
| `content` | `String` (`TEXT`) |
| `createdAt` | `ZonedDateTime` |

## Files Service (`files`)

No JPA entities (`@Entity`) currently defined.

## Relationship Types (Conceptual Across System)

1. `Article (1) -> (N) ArticleLike`  
   Type: one-to-many inside `articles`.

2. `Article (1) -> (N) ArticleImage` (via `articleId`)  
   Type: one-to-many inside `articles` (stored as UUID reference).

3. `CustomUserDetails (1) -> (N) Article` (via `Article.authorId`)  
   Type: one-to-many across services `auth -> articles` (UUID reference).

4. `CustomUserDetails (1) -> (N) ArticleLike` (via `ArticleLike.userId`)  
   Type: one-to-many across services `auth -> articles` (UUID reference).

5. `CustomUserDetails (N) <-> (N) CustomUserDetails` via `UserFollow`  
   Type: many-to-many self-reference in `auth` via join entity.

6. `Article (1) -> (N) ArticleAssistantChat` (via `articleId`)  
   Type: one-to-many across services `articles -> llm` (UUID reference).

7. `ArticleAssistantChat (1) -> (N) ArticleAssistantMessage`  
   Type: one-to-many inside `llm`.

8. `Article (N) <-> (N) CustomUserDetails` via `ArticleLike`  
   Type: conceptual many-to-many between articles and users via join entity.
