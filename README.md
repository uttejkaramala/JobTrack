# JobTrack

> A full-stack job application management platform built with **Spring
> Boot** and **React** to help job seekers organize applications,
> interviews, follow-ups, notes, and application progress in one place.

## Overview

JobTrack is a full-stack web application designed to make the job-search
process easier to manage.

Instead of tracking applications across spreadsheets, browser tabs,
notes, and reminders, JobTrack provides a centralized workspace where
users can:

-   Create and manage job applications
-   Track application status and priority
-   Record interviews and interview rounds
-   Add notes to applications
-   Manage follow-up dates
-   View upcoming interviews
-   Monitor application statistics through a dashboard
-   Secure their account using JWT-based authentication

The project was built with a focus on **clean architecture, secure REST
APIs, user-level data ownership, validation, and a practical React
frontend**.

------------------------------------------------------------------------

## Features

### Authentication & Account Security

-   User registration and login
-   JWT-based stateless authentication
-   BCrypt password hashing
-   Protected REST APIs
-   Custom Spring Security configuration
-   Custom `401 Unauthorized` and `403 Forbidden` responses
-   Current-user resolution through the authenticated security context
-   Change-password functionality
-   Profile management
-   User-level ownership checks to prevent unauthorized access

### Job Application Management

-   Create, view, update, and delete applications
-   Track application status:
    -   Saved
    -   Applied
    -   Screening
    -   Interview
    -   Offer
    -   Rejected
    -   Withdrawn
-   Application priority:
    -   Low
    -   Medium
    -   High
-   Work mode:
    -   Remote
    -   Hybrid
    -   On-site
-   Employment type:
    -   Full-time
    -   Internship
    -   Contract
    -   Part-time
-   Salary range tracking
-   Job source tracking
-   Job URL and location
-   Applied date
-   Next follow-up date
-   Search, filtering, and pagination support

### Interview Management

-   Add interviews to applications
-   Track interview rounds
-   Schedule interview dates
-   Interview types:
    -   Online
    -   Phone
    -   In-person
-   Interview status:
    -   Scheduled
    -   Completed
    -   Cancelled
    -   Rescheduled
-   Add interview notes
-   View upcoming interviews

### Notes

-   Add notes to job applications
-   Edit notes
-   Delete notes
-   Notes are associated with the relevant application

### Follow-ups

-   Track overdue follow-ups
-   View today's follow-ups
-   View upcoming follow-ups

### Dashboard

The dashboard provides an overview of the user's job search, including:

-   Total applications
-   Applications by status
-   Follow-up information
-   Upcoming interviews
-   Applications by source
-   Applications by work mode
-   Applications by employment type

### Frontend UX

-   Responsive React interface
-   Application pipeline view
-   Application details page
-   Loading skeletons
-   Empty states
-   Error states
-   Toast notifications
-   Modal-based actions
-   Smooth UI interactions
-   Clean, minimal visual design
-   Dedicated About page

------------------------------------------------------------------------

## Architecture

``` text
                         ┌──────────────────────┐
                         │      React UI        │
                         │   Vite + JavaScript  │
                         └──────────┬───────────┘
                                    │
                                    │ HTTP / JSON
                                    ▼
                         ┌──────────────────────┐
                         │       Axios          │
                         │ JWT Request Interceptor
                         └──────────┬───────────┘
                                    │
                              Bearer JWT
                                    │
                                    ▼
                  ┌─────────────────────────────────┐
                  │       Spring Security           │
                  │                                 │
                  │ JWT Authentication Filter       │
                  │ Authentication / Authorization  │
                  │ CORS                            │
                  └───────────────┬─────────────────┘
                                  │
                                  ▼
                  ┌─────────────────────────────────┐
                  │          REST Controllers       │
                  └───────────────┬─────────────────┘
                                  │
                                  ▼
                  ┌─────────────────────────────────┐
                  │             Services            │
                  │ Business Logic + Validation     │
                  └───────────────┬─────────────────┘
                                  │
                                  ▼
                  ┌─────────────────────────────────┐
                  │       Spring Data JPA            │
                  │       Repositories              │
                  └───────────────┬─────────────────┘
                                  │
                                  ▼
                         ┌──────────────────────┐
                         │      PostgreSQL      │
                         └──────────────────────┘
```

### Request Flow

A typical authenticated request follows this flow:

``` text
React
  ↓
Axios
  ↓
Authorization: Bearer <JWT>
  ↓
JwtAuthenticationFilter
  ↓
Spring Security
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
PostgreSQL
  ↓
Response DTO
  ↓
React
```

------------------------------------------------------------------------

## Project Structure

``` text
JobTrack/
│
├── BACKEND/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/jobtrack/
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── exception/
│   │   │   │   ├── repository/
│   │   │   │   ├── security/
│   │   │   │   ├── service/
│   │   │   │   └── specification/
│   │   │   └── resources/
│   │   └── test/
│   │       └── java/com/jobtrack/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── FRONTEND/
│   ├── src/
│   │   ├── components/
│   │   ├── context/
│   │   ├── layouts/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── styles.css
│   ├── package.json
│   └── package-lock.json
│
└── .gitignore
```

------------------------------------------------------------------------

## Tech Stack

### Backend

| Technology | Purpose |
|---|---|
| Java 17 | Backend language |
| Spring Boot | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| BCrypt | Password hashing |
| Bean Validation | Request validation |
| PostgreSQL | Relational database |
| Maven | Build and dependency management |
| Lombok | Boilerplate reduction |
| Springdoc OpenAPI | Swagger API documentation |

### Frontend

| Technology | Purpose |
|---|---|
| React | User interface |
| Vite | Frontend build tooling |
| JavaScript | Application logic |
| Axios | HTTP communication |
| React Router | Client-side routing |
| Lucide React | UI icons |
| CSS | Styling and responsive layout |

---

## Database Model

The core domain consists of the following entities:

``` text
User
 │
 └──< JobApplication
          │
          ├──< Interview
          │
          └──< ApplicationNote
```

### Main Relationships

-   One `User` can have many `JobApplication` records.
-   One `JobApplication` can have many `Interview` records.
-   One `JobApplication` can have many `ApplicationNote` records.
-   Child entities reference their parent application.
-   Ownership checks ensure users can only access their own application
    data.

The backend intentionally avoids exposing JPA entities directly through
REST responses. DTOs are used to control API responses and avoid issues
such as recursive JSON serialization.

------------------------------------------------------------------------

## Security

JobTrack uses stateless JWT authentication.

### Authentication Flow

``` text
1. User registers
       ↓
2. Password is hashed using BCrypt
       ↓
3. User logs in
       ↓
4. Spring Security authenticates credentials
       ↓
5. JWT is generated
       ↓
6. Frontend stores the token
       ↓
7. Axios sends Bearer token with protected requests
       ↓
8. JWT filter validates the token
       ↓
9. Spring Security establishes authentication
       ↓
10. Controller/service processes the request
```

### Security Measures

-   Passwords are never stored as plain text
-   BCrypt password hashing
-   JWT-based stateless authentication
-   Protected API endpoints
-   Custom authentication entry point
-   Custom access denied handler
-   Ownership validation
-   Request validation
-   Global exception handling
-   CORS configuration
-   Secrets stored outside source code using environment variables

------------------------------------------------------------------------

## API Overview

### Authentication

``` text
POST /api/auth/register
POST /api/auth/login
```

### Applications

``` text
POST   /api/applications
GET    /api/applications
GET    /api/applications/{id}
PUT    /api/applications/{id}
DELETE /api/applications/{id}

PATCH  /api/applications/{id}/status
GET    /api/applications/follow-ups
```

### Interviews

``` text
POST   /api/applications/{applicationId}/interviews
GET    /api/applications/{applicationId}/interviews
GET    /api/interviews/{id}
PUT    /api/interviews/{id}
DELETE /api/interviews/{id}
PATCH  /api/interviews/{id}/status
```

### Notes

``` text
POST   /api/applications/{applicationId}/notes
GET    /api/applications/{applicationId}/notes
PUT    /api/notes/{id}
DELETE /api/notes/{id}
```

### Dashboard

``` text
GET /api/dashboard/summary
```

Swagger/OpenAPI documentation is available when the backend is running:

``` text
http://localhost:8080/swagger-ui/index.html
```

------------------------------------------------------------------------

## Getting Started

### Prerequisites

Make sure the following are installed:

-   Java 17+
-   Maven (or use the included Maven Wrapper)
-   Node.js
-   npm
-   PostgreSQL
-   Git

------------------------------------------------------------------------

## Backend Setup

Navigate to the backend:

``` bash
cd BACKEND
```

Create a `.env` file:

``` env
DB_USERNAME=your_postgres_username
DB_PASSWORD=your_postgres_password
JWT_SECRET=your_secure_jwt_secret
JWT_EXPIRATION=3600000
```

The `.env` file is intentionally excluded from Git.

Create the PostgreSQL database:

``` sql
CREATE DATABASE jobtrack_db;
```

Then start the backend.

### Windows

``` bash
mvnw.cmd spring-boot:run
```

### macOS / Linux

``` bash
./mvnw spring-boot:run
```

The backend runs on:

``` text
http://localhost:8080
```

------------------------------------------------------------------------

## Frontend Setup

Navigate to the frontend:

``` bash
cd FRONTEND
```

Install dependencies:

``` bash
npm install
```

Create a `.env` file:

``` env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_DEMO_MODE=false
```

Start the development server:

``` bash
npm run dev
```

The frontend will normally be available at:

``` text
http://localhost:5173
```

------------------------------------------------------------------------

## Environment Variables

### Backend

Create:

``` text
BACKEND/.env
```

Example:

``` env
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your_secret
JWT_EXPIRATION=3600000
```

### Frontend

Create:

``` text
FRONTEND/.env
```

Example:

``` env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_DEMO_MODE=false
```

A frontend `.env.example` file is included in the repository to show the
required configuration without exposing local environment values.

> Never commit real passwords, JWT secrets, database credentials, API
> keys, or other private configuration values.

------------------------------------------------------------------------

## Testing

The backend contains service-layer tests covering important business
logic, including:

-   Job application operations
-   User registration and duplicate-email handling
-   Password changes
-   Interview creation and ownership validation
-   Application note ownership validation
-   Dashboard aggregation

Run the backend tests with:

``` bash
cd BACKEND
mvn test
```

or:

``` bash
./mvnw test
```

On Windows:

``` bash
mvnw.cmd test
```

------------------------------------------------------------------------

## Design Decisions

### DTOs instead of exposing entities

The API uses DTOs to control request and response data.

This helps:

-   Prevent accidental exposure of entity internals
-   Avoid recursive JSON serialization
-   Keep API contracts separate from persistence models
-   Validate incoming requests cleanly

### Service layer

Business logic is kept in services rather than controllers.

``` text
Controller
    ↓
Service
    ↓
Repository
```

This keeps controllers focused on HTTP concerns and makes business logic
easier to test.

### Ownership validation

Every authenticated user's application data is protected by ownership
checks.

For example, application, interview, and note operations verify that the
requested resource belongs to the current user.

This prevents a user from accessing another user's data simply by
changing an ID in an API request.

### Stateless authentication

The application uses JWT authentication instead of server-side sessions.

This allows the React frontend and Spring Boot backend to communicate
through stateless REST APIs.

------------------------------------------------------------------------

## Error Handling

JobTrack uses centralized exception handling through a global exception
handler.

The backend provides structured responses for cases such as:

-   Resource not found
-   Duplicate resources
-   Validation failures
-   Invalid request data
-   Authentication failures
-   Access denied errors

This keeps error responses consistent across the REST API.

------------------------------------------------------------------------

## Development Highlights

This project was built to practice real-world full-stack development
concepts rather than only basic CRUD operations.

Key areas covered include:

-   REST API design
-   Spring Security
-   JWT authentication
-   Password hashing
-   Role/authority-based security
-   User-level resource ownership
-   DTO-based API design
-   Validation
-   Exception handling
-   CORS
-   Spring Data JPA
-   PostgreSQL
-   Pagination and filtering
-   Specification-based searching
-   Dashboard aggregation
-   React API integration
-   Protected frontend routes
-   Loading and error states
-   Service-layer testing

------------------------------------------------------------------------

## Future Improvements

Potential future improvements include:

-   Production deployment
-   Automated CI/CD
-   Email reminders for interviews and follow-ups
-   Resume management
-   Job description analysis
-   Application analytics over time
-   Calendar integration
-   Advanced search and filtering
-   Automated job-board integrations
-   Role-based administration

------------------------------------------------------------------------

## Author

**Uttej Karamala**

Computer Science & Engineering\
AI & Machine Learning

Focused on:

-   Java
-   Spring Boot
-   Spring Security
-   REST APIs
-   React
-   PostgreSQL
-   Full-stack development

------------------------------------------------------------------------

## License

This project is currently intended as a personal portfolio and learning
project.
