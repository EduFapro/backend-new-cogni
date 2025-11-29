# API Documentation

This backend includes interactive API documentation using Swagger/OpenAPI.

## Accessing the Documentation

Once the server is running, you can access the API documentation at:

### Swagger UI (Interactive Documentation)
```
http://localhost:8080/swagger
```

The Swagger UI provides:
- ✅ Interactive API explorer
- ✅ Try-it-out functionality for testing endpoints
- ✅ Request/response examples
- ✅ Schema definitions
- ✅ Authentication configuration

### OpenAPI Specification (Raw YAML)
```
http://localhost:8080/openapi
```

Or view the source file directly:
```
src/main/resources/openapi/documentation.yaml
```

## Available Endpoints

### Evaluators
- `POST /api/evaluators` - Create evaluator
- `GET /api/evaluators/{id}` - Get evaluator by ID
- `PUT /api/evaluators/{id}` - Update evaluator
- `DELETE /api/evaluators/{id}` - Delete evaluator

### Participants
- `POST /api/participants` - Create participant
- `GET /api/participants?evaluatorId={id}` - Get participants by evaluator
- `GET /api/participants/{id}` - Get participant by ID
- `PUT /api/participants/{id}` - Update participant
- `DELETE /api/participants/{id}` - Delete participant

### Evaluations
- `POST /api/evaluations` - Create evaluation
- `GET /api/evaluations/{id}` - Get evaluation by ID
- `PUT /api/evaluations/{id}` - Update evaluation
- `PATCH /api/evaluations/{id}/status` - Update status

### Module Instances
- `POST /api/module-instances` - Create module instance
- `GET /api/module-instances?evaluationId={id}` - Get by evaluation
- `GET /api/module-instances/{id}` - Get by ID
- `PATCH /api/module-instances/{id}/status` - Update status
- `DELETE /api/module-instances/{id}` - Delete

### Task Instances
- `POST /api/task-instances` - Create task instance
- `GET /api/task-instances?moduleInstanceId={id}` - Get by module instance
- `GET /api/task-instances/{id}` - Get by ID
- `PATCH /api/task-instances/{id}/complete` - Mark completed
- `DELETE /api/task-instances/{id}` - Delete

### Recording Files
- `POST /api/recordings` - Create recording metadata
- `GET /api/recordings/{id}` - Get by ID
- `GET /api/recordings/metadata/{taskInstanceId}` - Get by task instance
- `DELETE /api/recordings/{id}` - Delete

## Using Swagger UI

1. **Start the server**:
   ```bash
   gradle run
   ```

2. **Open Swagger UI**:
   Navigate to http://localhost:8080/swagger in your browser

3. **Test an endpoint**:
   - Click on any endpoint to expand it
   - Click "Try it out"
   - Fill in the required parameters
   - Click "Execute"
   - View the response

## Example Request

Using the Swagger UI, you can test creating an evaluator:

**Endpoint**: `POST /api/evaluators`

**Request Body**:
```json
{
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com",
  "birthDate": "1990-01-01",
  "specialty": "Psychology",
  "cpfOrNif": "12345678900",
  "username": "johndoe",
  "password": "securepassword",
  "firstLogin": true,
  "isAdmin": false
}
```

**Expected Response** (201 Created):
```json
{
  "id": 1,
  "message": "Evaluator created successfully"
}
```

## Data Models

All schemas are documented in the OpenAPI specification with:
- Required fields
- Field types and formats
- Example values
- Validation constraints
- Enum values (for status fields)

## Status Codes

The API uses standard HTTP status codes:
- `200 OK` - Successful request
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request or missing required fields
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Updating Documentation

To update the API documentation:

1. Edit the YAML file:
   ```
   src/main/resources/openapi/documentation.yaml
   ```

2. Rebuild the project:
   ```bash
   gradle build
   ```

3. Restart the server:
   ```bash
   gradle run
   ```

4. Refresh the Swagger UI in your browser

## OpenAPI Specification Version

This API uses OpenAPI 3.0.3 specification format.
