# Audition API — Implementation Notes

## 1. Overview

The Audition API is a Java 17 and Spring Boot REST application that retrieves posts and comments from the JSONPlaceholder upstream API.

The implementation focuses on clean architecture, input validation, consistent error handling, configurable HTTP communication, observability, automated testing, and code quality.

## 2. Technology Stack

- Java 17
- Spring Boot 3
- Gradle 8
- Spring Web and `RestTemplate`
- Jackson `ObjectMapper`
- Spring Boot Actuator
- Micrometer and Prometheus
- Micrometer Tracing
- JUnit and Mockito
- JaCoCo
- Checkstyle, PMD, and SpotBugs

## 3. Prerequisites

Install the following:

- Java 17
- Gradle 8 compatible with the project
- IntelliJ IDEA, VS Code, or another Java IDE

Verify the installed Java version from a terminal:

```powershell
java -version
```

Verify the project's Gradle wrapper:

```powershell
.\gradlew.bat --version
```

## 4. Build, Test, and Run

Run all configured verification tasks from the project root.

### Build and verify

```powershell
.\gradlew.bat clean check
```

This runs the configured tests, coverage verification, and code quality checks.

### Generate the JaCoCo report

```powershell
.\gradlew.bat test jacocoTestReport
```

Open the HTML report at:

`build/reports/jacoco/test/html/index.html`

The project is configured to enforce a minimum line coverage threshold of 80%.

### Start the application

```powershell
.\gradlew.bat bootRun
```

The application is available at:

`http://localhost:8080`

Stop the application using `Ctrl+C` in the terminal where it is running.

## 5. REST API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/posts` | Retrieves all posts |
| GET | `/posts?title=sunt` | Filters posts by title |
| GET | `/posts/{id}` | Retrieves a post by ID |
| GET | `/posts/{id}/with-comments` | Retrieves a post with its comments |
| GET | `/posts/{id}/comments` | Retrieves comments for a post |
| GET | `/actuator/health` | Checks application health |
| GET | `/actuator/prometheus` | Exposes Prometheus metrics |

### 5.1 Retrieve all posts

**Request**

```http
GET /posts
```

**Command**

```powershell
curl.exe -i http://localhost:8080/posts
```

Returns a JSON array containing posts from the upstream service.

### 5.2 Retrieve a post by ID

**Request**

```http
GET /posts/1
```

**Command**

```powershell
curl.exe -i http://localhost:8080/posts/1
```

**Sample response — HTTP 200 OK**

```json
{
  "userId": 1,
  "id": 1,
  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
  "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
}
```

### 5.3 Retrieve a post with comments

**Request**

```http
GET /posts/1/with-comments
```

**Command**

```powershell
curl.exe -i http://localhost:8080/posts/1/with-comments
```

Returns the post together with its associated comments.

The response contains the post fields and a `comments` array. The actual comment values depend on the upstream API response.

### 5.4 Retrieve comments for a post

**Request**

```http
GET /posts/1/comments
```

**Command**

```powershell
curl.exe -i http://localhost:8080/posts/1/comments
```

**Example response structure**

```json
[
  {
    "postId": 1,
    "id": 1,
    "name": "Example comment",
    "email": "commenter@example.com",
    "body": "Example comment body"
  }
]
```

The comment values above are illustrative. The actual endpoint returns the comments supplied by the upstream API.

### 5.5 Filter posts by title

**Request**

```http
GET /posts?title=sunt
```

**Command**

```powershell
curl.exe -i "http://localhost:8080/posts?title=sunt"
```

Returns posts whose titles contain the search text, using case-insensitive matching.

### 5.6 Check application health

**Request**

```http
GET /actuator/health
```

**Command**

```powershell
curl.exe -i http://localhost:8080/actuator/health
```

**Sample response**

```json
{
  "status": "UP"
}
```

### 5.7 Retrieve Prometheus metrics

**Request**

```http
GET /actuator/prometheus
```

**Command**

```powershell
curl.exe -i http://localhost:8080/actuator/prometheus
```

Returns metrics in Prometheus text format, including HTTP request statistics, JVM memory, threads, CPU usage, and logging event counts.

## 6. Application Architecture

The application follows a layered structure.

- **Controller layer:** Handles REST requests and validates post IDs.
- **Integration layer:** Uses `RestTemplate` to communicate with the upstream JSONPlaceholder API.
- **Configuration layer:** Configures the HTTP client, JSON handling, timeouts, and outbound request logging.
- **Exception handling:** Uses `@RestControllerAdvice` and Spring `ProblemDetail` for structured error responses.

This separation improves maintainability, testability, and readability.

## 7. Input Validation and Error Handling

Post IDs must be positive integers. Invalid IDs return HTTP 400 Bad Request.

The application handles missing posts, unsupported HTTP methods, invalid requests, and upstream communication failures. Depending on the failure, responses can include HTTP 400, 404, 405, or 502.

Unexpected errors should return safe client-facing messages without exposing sensitive internal details.

## 8. Configuration

The upstream API URL and connection/read timeouts can be configured through environment variables.

| Variable | Purpose | Default |
|---|---|---|
| `API_BASE_URL` | Upstream API base URL | `https://jsonplaceholder.typicode.com` |
| `API_CONNECT_TIMEOUT` | Connection timeout | `5s` |
| `API_READ_TIMEOUT` | Read timeout | `5s` |

These settings allow the application to be configured for different environments without changing source code.

## 9. Logging, Tracing, and Metrics

### Logging

SLF4J is used for application and outbound HTTP logging. Outbound logs include request methods, URIs, response status codes, and communication failures.

### Tracing

Micrometer Tracing dependencies are configured. A response filter adds `X-Trace-Id` and `X-Span-Id` headers when a current tracing span is available.

End-to-end trace propagation to outbound HTTP requests should be verified separately.

### Metrics

Spring Boot Actuator and Micrometer expose application health and metrics. Prometheus metrics include HTTP request statistics and JVM resource information.

For production deployment, management endpoints should be appropriately secured and sensitive information should not be logged.

## 10. Testing and Code Quality

Unit tests cover application behaviour, configuration, exception handling, and tracing response headers.

JaCoCo generates HTML and XML reports and enforces a configured minimum line coverage of 80%. Checkstyle, PMD, SpotBugs, and Gradle verification tasks support code quality.

Run the complete verification command:

```powershell
.\gradlew.bat clean check
```

## 11. Potential Production Improvements

- Add integration tests using a mock HTTP server to test upstream responses, timeouts, and failures.
- Verify end-to-end distributed tracing for outbound HTTP requests.
- Consider retry and circuit-breaker policies where appropriate.
- Secure management endpoints and review production logging settings.
- Add automated API documentation and deployment pipeline checks.