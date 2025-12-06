@echo off
setlocal ENABLEDELAYEDEXPANSION

REM ================================
REM Backend Test Script (DEV Mode)
REM ================================

set BASE_URL=http://localhost:8080

echo ===========================================
echo   BACKEND API DEV TEST (CMD / curl.exe)
echo ===========================================
echo.

REM 1. STATUS CHECK
echo [1] GET /api/status
curl -s "%BASE_URL%/api/status"
echo.
echo -------------------------------------------

REM 2. CREATE EVALUATOR
echo [2] POST /api/evaluators
curl -s -X POST "%BASE_URL%/api/evaluators" ^
  -H "Content-Type: application/json" ^
  -d "{""name"":""John"",""surname"":""Doe"",""email"":""john.doe@example.com"",""birthDate"":""1990-01-01"",""specialty"":""Psychology"",""cpfOrNif"":""12345678900"",""username"":""johndoe"",""password"":""password123"",""firstLogin"":true,""isAdmin"":false}"
echo.
echo (Assuming evaluator got ID 1 in DEV)
echo -------------------------------------------

REM 3. LOGIN
echo [3] POST /api/evaluators/login
curl -s -X POST "%BASE_URL%/api/evaluators/login" ^
  -H "Content-Type: application/json" ^
  -d "{""username"":""johndoe"",""password"":""password123""}"
echo.
echo -------------------------------------------

REM 4. CREATE PARTICIPANT
echo [4] POST /api/participants
curl -s -X POST "%BASE_URL%/api/participants" ^
  -H "Content-Type: application/json" ^
  -d "{""name"":""Alice"",""surname"":""Smith"",""birthDate"":""2015-05-20"",""sex"":2,""educationLevel"":1,""laterality"":1,""evaluatorId"":1}"
echo.
echo (Assuming participant got ID 1)
echo -------------------------------------------

REM 5. CREATE EVALUATION
echo [5] POST /api/evaluations
curl -s -X POST "%BASE_URL%/api/evaluations" ^
  -H "Content-Type: application/json" ^
  -d "{""participantId"":1,""evaluatorId"":1,""evaluationDate"":""2023-10-27T10:00:00"",""status"":1,""language"":1}"
echo.
echo (Assuming evaluation got ID 1)
echo -------------------------------------------

REM 6. CREATE MODULE INSTANCE
echo [6] POST /api/module-instances
curl -s -X POST "%BASE_URL%/api/module-instances" ^
  -H "Content-Type: application/json" ^
  -d "{""evaluationId"":1,""moduleId"":1,""status"":1,""startTime"":""2023-10-27T10:00:00""}"
echo.
echo (Assuming moduleInstance got ID 1)
echo -------------------------------------------

REM 7. CREATE TASK INSTANCE
echo [7] POST /api/task-instances
curl -s -X POST "%BASE_URL%/api/task-instances" ^
  -H "Content-Type: application/json" ^
  -d "{""moduleInstanceId"":1,""taskId"":1,""status"":1,""startTime"":""2023-10-27T10:05:00""}"
echo.
echo (Assuming taskInstance got ID 1)
echo -------------------------------------------

REM 8. COMPLETE TASK
echo [8] PATCH /api/task-instances/1/complete
curl -s -X PATCH "%BASE_URL%/api/task-instances/1/complete" ^
  -H "Content-Type: application/json" ^
  -d "{""duration"":""120s""}"
echo.
echo -------------------------------------------

REM 9. CREATE RECORDING METADATA
echo [9] POST /api/recordings
curl -s -X POST "%BASE_URL%/api/recordings" ^
  -H "Content-Type: application/json" ^
  -d "{""taskInstanceId"":1,""filePath"":""/data/recordings/rec_001.mp4"",""fileSize"":1024000,""duration"":""120s"",""createdAt"":""2023-10-27T10:07:00""}"
echo.
echo -------------------------------------------

REM 10. GET RECORDING METADATA BY TASK INSTANCE
echo [10] GET /api/recordings/metadata/1
curl -s "%BASE_URL%/api/recordings/metadata/1"
echo.
echo -------------------------------------------

echo.
echo ==== ALL REQUESTS SENT ====
echo (Check responses above for details)
echo.

endlocal
