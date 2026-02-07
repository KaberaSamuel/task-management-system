# Task Management System

A backend application built with **Spring Boot 3.5** for efficient task and user management. This system provides a secure API for managing project tasks and tracking progress.

## Key Features

- **Task Management**: Full CRUD operations for tracking tasks.
- **DTO Architecture**: Uses Data Transfer Objects (DTOs) for secure and efficient data exchange.
- **JWT based authentication and authorization**
- **Global Error Handling**: Consistent API responses and clear error messaging via a global exception handler.


## Tech Stack

- **Java 17**
- **Spring Boot 3.5.0**
- **Spring Data JPA**
- **MySQL** (Database)
- **Maven** (Build Tool)

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.x
- MySQL Server

### Configuration

Create a `.env` file in the root directory and configure your database credentials:

```env
DB_URL=jdbc:mysql://localhost:3306/task_management
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Build and Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd task-management-system
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Reference

### Authentication

Endpoints for user registration, login, and logout.

#### Register User
`POST /auth/register`

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepassword",
  "role": "MEMBER"
}
```

**Response:**
- `201 Created`: "User created successfully!"

#### Login
`POST /auth/login`

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "securepassword"
}
```

**Response:**
- `200 OK`:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```

#### Logout
`POST /auth/logout`

*Requires Bearer Token in `Authorization` header.*

**Response:**
- `200 OK`: "Logged out successfully, token invalidated."

---

### Task Management

Full CRUD operations for tasks. All endpoints require a **Bearer Token**.

#### Get All Tasks
`GET /api/tasks`

**Response:**
- `200 OK`:
  ```json
  [
    {
      "title": "Setup Project",
      "description": "Initialize Spring Boot and MySQL",
      "status": "COMPLETED",
      "priority": "HIGH",
      "ownerEmail": "john@example.com",
      "ownerUsername": "johndoe"
    }
  ]
  ```

#### Create Task
`POST /api/tasks`

**Request Body:**
```json
{
  "title": "Implement Feature X",
  "description": "Details about feature X",
  "status": "TODO",
  "priority": "MEDIUM"
}
```

**Response:**
- `201 Created`

#### Update Task
`PUT /api/tasks/{id}`

**Request Body:**
```json
{
  "title": "Updated Title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "HIGH"
}
```

**Response:**
- `200 OK`

#### Delete Task
`DELETE /api/tasks/{id}`

**Response:**
- `204 No Content`

## Testing

The project includes a comprehensive test suite covering controllers and services.

To run all tests:
```bash
./mvnw test
```


