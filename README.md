# Ticket Tracking API

A RESTful API for managing projects, tickets, and user assignments — built as part of the HackYourFuture backend mid-project.

## Tech Stack

- Java 25
- Spring Boot 4.1.0
- PostgreSQL (Docker)
- JdbcTemplate
- Lombok
- Jakarta Validation
- Resend API (email notifications)
- Maven

## Features

- Full CRUD for users, projects, and tickets
- Assign/unassign users to tickets
- Filter tickets by text and status
- Email notifications to assignees on ticket updates (via Resend API)
- Input validation with meaningful error responses

## Getting Started

### Prerequisites

- Java 25
- Docker (for PostgreSQL)
- Maven or use the included `./mvnw` wrapper

### Setup

1. Clone the repository:
```bash
git clone https://github.com/YanaP1312/ticket-tracking-api.git
cd ticket-tracking-api
```

2. Start a PostgreSQL container:
```bash
docker run --name ticket-tracking-db \
  -e POSTGRES_DB=ticket_tracking \
  -e POSTGRES_USER=your_username \
  -e POSTGRES_PASSWORD=your_password \
  -p 5432:5432 \
  -d postgres
```

3. Set the following environment variables:

DB_URL=jdbc:postgresql://localhost:5432/ticket_tracking
DB_USERNAME=your_username
DB_PASSWORD=your_password
RESEND_API_KEY=your_resend_api_key

4. Run the SQL setup script to create the tables:

src/main/resources/setup-ticket-tracking-system.sql

5. Run the application:
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

## Running Tests

```bash
./mvnw verify
```

Runs 6 tests (3 unit + 3 integration) — no real database connection needed.

## API Documentation

Full endpoint descriptions are available in `src/main/resources/API_Design_Document.pdf`.

## Endpoints Overview

| Method | Path | Description |
|--------|------|-------------|
| GET | /users | Get all users |
| GET | /users/{id} | Get user by id |
| POST | /users | Create new user |
| PATCH | /users/{id} | Update user info |
| DELETE | /users/{id} | Remove user |
| GET | /projects | Get all projects with ticket counts |
| GET | /projects/{id} | Get project by id |
| POST | /projects | Create new project |
| PATCH | /projects/{id} | Update project name |
| DELETE | /projects/{id} | Remove project |
| GET | /tickets | Get all tickets (filterable) |
| GET | /tickets/{id} | Get ticket by id |
| POST | /tickets | Create new ticket |
| PATCH | /tickets/{id} | Update ticket |
| POST | /tickets/{id}/assignees | Add assignee |
| DELETE | /tickets/{id}/assignees/{userId} | Remove assignee |

