# Spring Boot Microservice Master Requirements

## Overview
This prompt defines guidelines for developing a Spring Boot microservice as a Senior Software Engineer. The goal is to deliver clean, maintainable, scalable, idiomatic Java 17 code following best practices. The assistant must strictly adhere to the constraints and architectural principles below.
* We are about to build a file storage microservice for uploading and downloading files. The service will allow users to share folders with other users via email.
---

## Context & Technology Stack
- Spring Boot 3.2.2
- Java 17
- Lombok
- SLF4J
- JUnit 5 with Mockito
- Maven 3
- Actuator

---

## Build Configuration
- Build tool: Maven 3
- JDK: 17
- Spring Boot version: 3.2.2
- Dependencies compatible with Java 17 and Spring Boot 3.2.2

---

## Global API Requirements

### Authentication & Tracing
- Build a securityFilterChain bean to configure security
  - allowing auth endpoints to be accessed without authentication 
  - Actuator shutdown endpoint only by ADMIN role
-  Build JWTTokenProvider for validating JWT tokens
-  JWT token authentication via "Authorization" header
-  Distributed tracing using "X-Correlation-ID" header
    - Use provided ID or generate UUID if missing
    - Include ID in MDC for logging
---

## Functional Requirements
 * POST `/api/v1/storage/share`
    - Request Body: JSON with `folderpath` (string) and `emails` (list of strings)
    - Response: HTTP 200 OK on success, HTTP 400 for validation errors, HTTP 500 for server errors
    - Functionality: Share the specified folder with the provided email addresses
 * GET `/api/v1/storage/download`
   - Query Parameter: `folderpath` (string)
   - Response: HTTP 200 OK with file stream, HTTP 400 for validation errors, HTTP 500 for server errors
   - Functionality: Download the specified folder as a ZIP file
---

### Domain Model

#### Core Entities
* User
    - id (Long)
    - email (String)
    - name (String)
    - roles (Set<String>)
* Folder
    - id (Long)
    - path (String)
    - owner (User)
    - sharedWith (Set<User>)
    - createdAt (LocalDateTime)
    - updatedAt (LocalDateTime)
* File
    - id (Long)
    - name (String)
    - path (String)
    - folder (Folder)
    - size (Long)
    - createdAt (LocalDateTime)
    - updatedAt (LocalDateTime)

---

## Business Rules
- Only the folder owner can share it
- Shared users can only download, not share further
- Folder paths must be unique per user
- Emails must be valid and registered users
- Folders can contain multiple files and subfolders

---

## Validation Requirements
- Non-null POST request DTO
- Valid, non-blank folderpath URL
- Non-null, non-empty emails list

---

## Error Handling & Resilience
- Use `@RestControllerAdvice` for centralized exception handling
- Return HTTP 400 for client errors and HTTP 500 for system errors
- Design for high TPS
- Implement retry mechanisms and circuit breakers

---

## DB Setup
- use postgres as default, H2 for local environment . Add required dependencies in pom.xml
- Environment variables: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- postgres Driver: `org.postgresql.Driver`
- H2 Driver: `org.h2.Driver`

---

### Dependency Injection
- Always use constructor injection (`@RequiredArgsConstructor`)
- Never use field injection

### Layered Architecture
- Separate layers: Controller, Service, Repository
- Follow SOLID principles

### Coding Practices
- Use Lombok
- Builder pattern for DTOs
- Specific imports only (no wildcards)
- Manage entity collections by clearing then adding new items

---

## Testing Strategy
- Controller tests for success and bad request
- Service tests for success and failure
- Mockito for mocking
- JUnit 5 for assertions
- Minimum 80% code coverage
- Postman collections covering all endpoints
- Focused unit tests mocking only direct dependencies

---    

## 🌟 MANDATORY CODING CONSTRAINTS AND BEST PRACTICES

**RULE:** Adhere strictly to these rules. Code must be clean, maintainable, scalable, and idiomatic.

### 1. Persistence & Entity Guidelines (JPA/Hibernate)

* **One-to-Many Setter (Orphan Deletion):** When modifying a one-to-many collection on an entity with `orphanRemoval = true`, **DO NOT** use a simple setter. **MANDATORY PATTERN:** You must manage the existing collection instance by calling **`entity.getCollection().clear()`** followed by **`entity.getCollection().addAll(newItems)`**. This prevents ORM collection manipulation issues.
* **Resource Management:** For all `Closeable` resources (e.g., Streams, Connections), **ALWAYS** use the **`try-with-resources`** statement. **NEVER** rely on manual `finally` blocks for closing.

### 2. Architectural Design & Dependency Management

* **Dependency Injection (DI):** **ALWAYS** use **Constructor Injection** for services and components (`private final MyService`). **NEVER** use field injection (e.g., `@Autowired` on a field). Constructor injection ensures dependencies are final and explicit.
* **Immutability:** **PREFER** immutable objects and utilize **Java Records** for simple Data Transfer Objects (DTOs). Local variables should be declared as **`final`** wherever possible to minimize side effects.

### 3. API Communication & Functional Purity

* **DTO Boundary Rule:** **NEVER** expose internal **Domain Entities** directly in API Controllers. **ALWAYS** map between **Entities** and dedicated **Data Transfer Objects (DTOs)** for all boundary communication (requests and responses).
* **Optional Usage:** **MANDATORY** use of functional methods (`.map()`, `.flatMap()`, `.orElseThrow()`, `.orElse()`) when handling `Optional<T>`. **AVOID** nesting `Optional` checks and **NEVER** call `.get()` without explicit checks.
* **Optional orElse Usage:** When using `Optional.orElse()`, ensure the alternative value is inexpensive to compute. If the alternative involves complex logic or resource-intensive operations, use `orElseGet(Supplier)` instead to defer execution until necessary.

### 4. Immutability & Safety
* **Immutability:** **PREFER** immutable objects and **Records** for DTOs. Declare local variables as **`final`** where possible.
* **Resource Management:** **ALWAYS** use the **`try-with-resources`** statement for all `Closeable` resources (Streams, Connections).

### 5. Unit Testing Strategy

* **Behavior Over Implementation:** Unit tests must validate the public **contract and behavior** of the class under test. **ONLY** mock the direct dependencies of the class. **AVOID** mocking simple value objects, DTOs, or internal private methods. Brittle tests coupled to implementation details are unacceptable.

### 6. Exception Handling 
* **Dont use generic exceptions**: Avoid using generic exceptions like `Exception` or `RuntimeException`. Instead, create and use specific exception classes that convey meaningful information about the error condition.
* **Checked vs Unchecked Exceptions**: Use checked exceptions for recoverable conditions that the caller can handle. Use unchecked exceptions for programming errors or conditions that are not expected to be caught.
* **Exception Chaining**: When rethrowing exceptions, use exception chaining to preserve the original exception context. This helps in debugging and understanding the root cause of the error.
* **As Hibernate doesn't rollback transaction for checked Exception.**: Use unchecked exceptions for service layer methods that are transactional to ensure proper rollback behavior.

---

## General Code Generation Guidelines
1. **Code Structure**
    - Single class per file
    - Short, focused methods
    - Meaningful naming
    - Avoid deep nesting

2. **Java Features**
    - Java 17 features (records, sealed classes)
    - Immutable where possible
    - Optional for null handling
    - Constants over magic numbers

3. **Logging**
    - SLF4J implementation
    - Appropriate log levels
    - Contextual information

4. **Quality Assurance**
    - No scaffolding errors
    - Proper file separation
    - Clean code principles

---

## Output Expectations
- Provide clear, working, testable, maintainable, and performant code that aligns with best engineering practices.
- Aim for clarity, simplicity, and extensibility in all generated solutions
- Address edge cases or ambiguity explicitly with clarifying assumptions or questions
- When ambiguity arises or inputs conflict, ask clarifying questions or state assumptions explicitly to ensure aligned expectations and correctness

---
