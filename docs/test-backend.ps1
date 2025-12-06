# ================================
# Backend Test Script (DEV Mode)
# ================================

$baseUrl = "http://localhost:8080"

function Call-API {
    param(
        [string]$Method,
        [string]$Endpoint,
        $Body = $null
    )

    Write-Host "`n==> $Method $Endpoint" -ForegroundColor Cyan

    try {
        if ($Body) {
            $json = $Body | ConvertTo-Json -Depth 5
            Write-Host "Payload:" $json -ForegroundColor DarkGray
        }

        $result = Invoke-RestMethod -Uri "$baseUrl$Endpoint" `
                                     -Method $Method `
                                     -ContentType "application/json" `
                                     -Body ($json)

        Write-Host "<== Response:" -ForegroundColor Green
        $result | ConvertTo-Json -Depth 5 | Write-Host
        return $result
    }
    catch {
        Write-Host "ERROR calling $Endpoint" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        exit 1
    }
}

Write-Host "=== Starting Backend Tests ===" -ForegroundColor Yellow


# --------------------------------------
# 1. Status Check
# --------------------------------------
$status = Call-API "GET" "/api/status"


# --------------------------------------
# 2. Create Evaluator
# --------------------------------------
$evaluator = Call-API "POST" "/api/evaluators" @{
    name       = "John"
    surname    = "Doe"
    email      = "john.doe@example.com"
    birthDate  = "1990-01-01"
    specialty  = "Psychology"
    cpfOrNif   = "12345678900"
    username   = "johndoe"
    password   = "password123"
    firstLogin = $true
    isAdmin    = $false
}

$evaluatorId = $evaluator.id


# --------------------------------------
# 3. Login Test
# --------------------------------------
$login = Call-API "POST" "/api/evaluators/login" @{
    username = "johndoe"
    password = "password123"
}


# --------------------------------------
# 4. Create Participant
# --------------------------------------
$participant = Call-API "POST" "/api/participants" @{
    name           = "Alice"
    surname        = "Smith"
    birthDate      = "2015-05-20"
    sex            = 2
    educationLevel = 1
    laterality     = 1
    evaluatorId    = $evaluatorId
}

$participantId = $participant.id


# --------------------------------------
# 5. Create Evaluation
# --------------------------------------
$evaluation = Call-API "POST" "/api/evaluations" @{
    participantId  = $participantId
    evaluatorId    = $evaluatorId
    evaluationDate = "2023-10-27T10:00:00"
    status         = 1
    language       = 1
}

$evaluationId = $evaluation.id


# --------------------------------------
# 6. Create Module Instance
# --------------------------------------
$moduleInstance = Call-API "POST" "/api/module-instances" @{
    evaluationId = $evaluationId
    moduleId     = 1
    status       = 1
    startTime    = "2023-10-27T10:00:00"
}

$moduleInstanceId = $moduleInstance.id


# --------------------------------------
# 7. Create Task Instance
# --------------------------------------
$taskInstance = Call-API "POST" "/api/task-instances" @{
    moduleInstanceId = $moduleInstanceId
    taskId           = 1
    status           = 1
    startTime        = "2023-10-27T10:05:00"
}

$taskInstanceId = $taskInstance.id


# --------------------------------------
# 8. Complete Task
# --------------------------------------
Call-API "PATCH" "/api/task-instances/$taskInstanceId/complete" @{
    duration = "120s"
}


# --------------------------------------
# 9. Create Recording Metadata
# --------------------------------------
Call-API "POST" "/api/recordings" @{
    taskInstanceId = $taskInstanceId
    filePath       = "/data/recordings/rec_001.mp4"
    fileSize       = 1024000
    duration       = "120s"
    createdAt      = "2023-10-27T10:07:00"
}


# --------------------------------------
# 10. Get Recording Metadata
# --------------------------------------
Call-API "GET" "/api/recordings/metadata/$taskInstanceId"


Write-Host "`n=== ALL TESTS COMPLETED SUCCESSFULLY ===" -ForegroundColor Yellow
