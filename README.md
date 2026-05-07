# Parking Manager

Backend application responsible for processing parking events in real time, managing parking spots occupancy and calculating parking revenue dynamically based on sector occupancy rate.

The project was built using Java 21 and Spring Boot following Clean Architecture principles and focusing on transactional consistency, testability and maintainability.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Cloud OpenFeign
- MapStruct
- Lombok
- MySQL
- H2 Database
- Docker
- Gradle
- JUnit 5
- Mockito

## Architecture

The application was structured using simplified [Clean Architecture](https://medium.com/@gabrielfernandeslemos/clean-architecture-uma-abordagem-baseada-em-princ%C3%ADpios-bf9866da1f9c).

Layers:

- Domain
    - Core business rules and entities
- Application
    - Use cases and orchestration
- Infrastructure
    - Database, HTTP clients and external integrations
- API
    - Controllers and request/response mapping

## Business Rules

### General Rules

- A parking spot becomes occupied only after a PARKED event.
- The first 30 minutes are free.
- After 30 minutes, the full hour price is charged.
- Parking price varies according to sector occupancy.
- A sector cannot exceed its maximum capacity.
- EXIT events release both spot and sector occupancy.

### Pricing Logic

Occupancy multiplier is dynamically calculated based on sector occupancy rate.

| Occupancy Rate | Multiplier |
|---|---|
| < 25% | 0.90 |
| <= 50% | 1.00 |
| <= 75% | 1.10 |
| > 75% | 1.25 |

## Running the Project

### Requirements

- Java 21
- Docker
- Docker Compose

### Running

```bash
docker compose up -d
```

### Explanation

When running the `docker-compose` command, all project dependencies will start automatically, including:

- MySQL database
- Garage simulator
- Parking Manager application

During startup, the application consumes the `/garage` endpoint provided by the simulator in order to create the parking sectors and spots locally.

After the initialization process, the application starts listening for parking events through the `/webhook` endpoint, processing vehicle entry and exit events in real time.

## API Endpoints

### Parking Webhook

POST `/webhook`

Processes parking events sent by simulator.

### Revenue

GET `/revenue?date=2026-05-05&sector=A`

Returns sector revenue for a given day.

## Tests

### Explanation

The project contains:

- Unit tests for:
    - Domain entities
    - Services
    - Use cases

- Integration tests for:
    - Controllers
    - Gateways
    - Transactional flows

### Running

```bash
./gradlew test
```

## Important Design Decisions

### Revenue calculation

Revenue is calculated dynamically from parking sessions instead of storing aggregated values.
This avoids data inconsistency and simplifies transactional logic.

### Idempotency

#### Garage Initialization

The garage synchronization flow avoids duplicated data during repeated executions.

- `Sector` entities are identified by their `name`;
- `Spot` entities are identified by their `externalId`.

This ensures the initialization can run multiple times safely.

#### Event Processing

Parking events are validated using entity status before processing.

Examples:

- a spot cannot be occupied twice;
- a parking session cannot be finished more than once.

This helps prevent duplicated event processing and inconsistent state transitions.

### Concurrency

Pessimistic locking was used for spot and sector updates in order to avoid race conditions during concurrent `PARKED` events.

This approach prioritizes data consistency over throughput, ensuring that multiple vehicles cannot occupy the same parking spot simultaneously.

As a trade-off, pessimistic locks may increase database contention and reduce performance under high concurrency scenarios.