# Manual API Testing Guide (DEV Mode)

Since the backend is in **DEV Mode**, authentication is bypassed. You can use `curl` or Postman to test these endpoints directly.

**Base URL**: `http://localhost:8080`

## 1. Status Check
Verify the backend is running and in DEV mode.
```bash
curl http://localhost:8080/api/status
```
**Expected Response**: `{"status":"ok","mode":"DEV"}`

---

## 2. Evaluators (Users)

### Create Evaluator
```bash
curl -X POST http://localhost:8080/api/evaluators \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John",
    "surname": "Doe",
    "email": "john.doe@example.com",
    "birthDate": "1990-01-01",
    "specialty": "Psychology",
    "cpfOrNif": "12345678900",
    "username": "johndoe",
    "password": "password123",
    "firstLogin": true,
    "isAdmin": false
  }'
```

### Get Evaluator
```bash
curl http://localhost:8080/api/evaluators/1
```

### Login (Public)
```bash
curl -X POST http://localhost:8080/api/evaluators/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

---

## 3. Participants

### Create Participant
```bash
curl -X POST http://localhost:8080/api/participants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice",
    "surname": "Smith",
    "birthDate": "2015-05-20",
    "sex": 2,
    "educationLevel": 1,
    "laterality": 1,
    "evaluatorId": 1
  }'
```
*Sex: 1=Male, 2=Female, 3=Other*
*Laterality: 1=Right, 2=Left, 3=Both*
*Education: 1=None, 2=Elementary, 3=Middle, 4=High, 5=College*

### Get Participant
```bash
curl http://localhost:8080/api/participants/1
```

### List Participants by Evaluator
```bash
curl "http://localhost:8080/api/participants?evaluatorId=1"
```

---

## 4. Evaluations

### Create Evaluation
```bash
curl -X POST http://localhost:8080/api/evaluations \
  -H "Content-Type: application/json" \
  -d '{
    "participantId": 1,
    "evaluatorId": 1,
    "evaluationDate": "2023-10-27T10:00:00",
    "status": 1,
    "language": 1
  }'
```
*Status: 1=Pending, 2=InProgress, 3=Completed*
*Language: 1=Portuguese, 2=English (Example)*

### Update Status
```bash
curl -X PATCH http://localhost:8080/api/evaluations/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": 2}'
```

---

## 5. Module Instances

### Create Module Instance
```bash
curl -X POST http://localhost:8080/api/module-instances \
  -H "Content-Type: application/json" \
  -d '{
    "evaluationId": 1,
    "moduleId": 1,
    "status": 1,
    "startTime": "2023-10-27T10:00:00"
  }'
```

### List by Evaluation
```bash
curl "http://localhost:8080/api/module-instances?evaluationId=1"
```

---

## 6. Task Instances

### Create Task Instance
```bash
curl -X POST http://localhost:8080/api/task-instances \
  -H "Content-Type: application/json" \
  -d '{
    "moduleInstanceId": 1,
    "taskId": 1,
    "status": 1,
    "startTime": "2023-10-27T10:05:00"
  }'
```

### List by Module Instance
```bash
curl "http://localhost:8080/api/task-instances?moduleInstanceId=1"
```

### Complete Task
```bash
curl -X PATCH http://localhost:8080/api/task-instances/1/complete \
  -H "Content-Type: application/json" \
  -d '{"duration": "120s"}'
```

---

## 7. Recordings

### Create Recording Metadata
```bash
curl -X POST http://localhost:8080/api/recordings \
  -H "Content-Type: application/json" \
  -d '{
    "taskInstanceId": 1,
    "filePath": "/data/recordings/rec_001.mp4",
    "fileSize": 1024000,
    "duration": "120s",
    "createdAt": "2023-10-27T10:07:00"
  }'
```

### Get Recording by Task Instance
```bash
curl http://localhost:8080/api/recordings/metadata/1
```
