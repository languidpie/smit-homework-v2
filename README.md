# Inventory Management System

A web application for managing two inventory collections:
- **Vinyl Records** - Katrin's record collection
- **Bicycle Parts** - Mart's bike parts inventory

## Prerequisites

- Java 21
- Node.js 18+

## Quick Start

### 1. Start the Backend

```bash
./gradlew run
```

The backend runs at **http://localhost:8080**

### 2. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at **http://localhost:5173**

## API Documentation

### Swagger UI

Interactive API documentation is available at:

**http://localhost:8080/swagger-ui**

Use Swagger UI to:
- Browse all available endpoints
- Test API requests directly in the browser
- View request/response schemas

### OpenAPI Specification

The raw OpenAPI spec (YAML) is available at:

**http://localhost:8080/swagger/inventory-management-api-1.0.yml**

## API Endpoints

### Vinyl Records (`/api/records`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/records` | List all records |
| GET | `/api/records/{id}` | Get a record |
| POST | `/api/records` | Create a record |
| PUT | `/api/records/{id}` | Update a record |
| DELETE | `/api/records/{id}` | Delete a record |
| GET | `/api/records/search?q=` | Search by title/artist |
| GET | `/api/records/genre/{genre}` | Filter by genre |

### Bicycle Parts (`/api/parts`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/parts` | List all parts |
| GET | `/api/parts/{id}` | Get a part |
| POST | `/api/parts` | Create a part |
| PUT | `/api/parts/{id}` | Update a part |
| DELETE | `/api/parts/{id}` | Delete a part |
| GET | `/api/parts/search?q=` | Search by name |
| GET | `/api/parts/type/{type}` | Filter by type |

## Running Tests

```bash
# Backend tests
./gradlew test

# Frontend tests
cd frontend
npm run test
```
