# Inventory Management System

A web application for managing two inventory collections with role-based access control:
- **Bicycle Parts** - Mart's bike parts inventory
- **Vinyl Records** - Katrin's record collection

## Prerequisites

- Java 21
- Node.js 18+
- Docker (for PostgreSQL)

## Quick Start

### 1. Start PostgreSQL

```bash
docker-compose up -d postgres
```

### 2. Start the Backend

```bash
./gradlew run
```

The backend runs at **http://localhost:8080**

### 3. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at **http://localhost:5173**

## Authentication

The application uses Basic Auth with two predefined users:

| User | Password | Access |
|------|----------|--------|
| mart | mart123 | Bicycle Parts (`/api/parts`) |
| katrin | katrin123 | Vinyl Records (`/api/records`) |

Each user can only access their own inventory section.

## API Documentation

### Swagger UI

Interactive API documentation is available at:

**http://localhost:8080/swagger-ui**

Note: Swagger UI requires authentication. Use one of the credentials above.

### OpenAPI Specification

The raw OpenAPI spec (YAML) is available at:

**http://localhost:8080/swagger/inventory-management-api-1.0.yml**

## API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/auth/me` | Get current user info |

### Vinyl Records (`/api/records`) - Katrin only

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/records` | List all records |
| GET | `/api/records/{id}` | Get a record |
| POST | `/api/records` | Create a record |
| PUT | `/api/records/{id}` | Update a record |
| DELETE | `/api/records/{id}` | Delete a record |
| GET | `/api/records/search?q=` | Search by title/artist |
| GET | `/api/records/genre/{genre}` | Filter by genre |

### Bicycle Parts (`/api/parts`) - Mart only

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/parts` | List all parts |
| GET | `/api/parts/{id}` | Get a part |
| POST | `/api/parts` | Create a part |
| PUT | `/api/parts/{id}` | Update a part |
| DELETE | `/api/parts/{id}` | Delete a part |
| GET | `/api/parts/search?q=` | Search by name |
| GET | `/api/parts/type/{type}` | Filter by type |

## Running with Docker

To run the entire stack with Docker:

```bash
docker-compose up
```

This starts:
- PostgreSQL on port 5432
- Backend on port 8080
- Frontend on port 80

## Running Tests

```bash
# Backend tests (requires Docker for Testcontainers)
./gradlew test

# Frontend tests
cd frontend
npm run test
```
