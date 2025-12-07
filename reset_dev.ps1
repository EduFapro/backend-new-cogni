Write-Host "Stopping Docker containers..." -ForegroundColor Yellow
docker-compose down

Write-Host "Checking for process on port 8080..." -ForegroundColor Yellow
$process = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique

if ($process) {
    Write-Host "Found process with PID: $process. Killing it..." -ForegroundColor Red
    Stop-Process -Id $process -Force
    Write-Host "Process killed." -ForegroundColor Green
}
else {
    Write-Host "No process found on port 8080." -ForegroundColor Green
}

Write-Host "Development environment reset complete." -ForegroundColor Cyan
