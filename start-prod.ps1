# -------------------------------------
# Step 1: Kill Spring Boot process on port 8080
# -------------------------------------
Write-Output "[STEP 1] Stopping existing Spring Boot process on port 8080..."

try {
    $springPid = (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess
    if ($springPid) {
        Write-Output "[STEP 1] Found PID $springPid. Terminating process..."
        Stop-Process -Id $springPid -Force
        Write-Output "[STEP 1] Process $springPid terminated."
    } else {
        Write-Output "[STEP 1] No process is using port 8080."
    }
} catch {
    Write-Output "[STEP 1] Error while checking/stopping process: $_"
}

# -------------------------------------
# Step 2: Define base path
# -------------------------------------
Write-Output "[STEP 2] Resolving base path..."

$base = Split-Path -Parent $MyInvocation.MyCommand.Path

# -------------------------------------
# Step 3: Copy JAR from Jenkins workspace to deploy target
# -------------------------------------
Write-Output "[STEP 3] Copying JAR file..."

$jenkinsJar = "C:\ProgramData\Jenkins\.jenkins\workspace\deploy-backend\target\gokim-api-1.0.0.jar"
$deployJar = "C:\app\backend\target\gokim-api-1.0.0.jar"

try {
    Copy-Item -Path $jenkinsJar -Destination $deployJar -Force
    Write-Output "[STEP 3] JAR copied to $deployJar"
} catch {
    Write-Output "[STEP 3] Failed to copy JAR file: $_"
    exit 1
}

# -------------------------------------
# Step 4: Copy latest start-prod.ps1 to deploy folder
# -------------------------------------
Write-Output "[STEP 4] Copying latest start-prod.ps1..."

try {
    Copy-Item "C:\ProgramData\Jenkins\.jenkins\workspace\deploy-backend\start-prod.ps1" `
        -Destination "C:\app\backend\deploy\start-prod.ps1" -Force
    Write-Output "[STEP 4] Script copied."
} catch {
    Write-Output "[STEP 4] Failed to copy script: $_"
    exit 1
}

# -------------------------------------
# Step 5: Load environment variables from .env
# -------------------------------------
Write-Output "[STEP 5] Loading environment variables..."

$envPath = Join-Path $base ".env"
$logDir = Join-Path $base "..\logs"
$jarPath = $deployJar

if (-Not (Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir | Out-Null
    Write-Output "[STEP 5] Log directory created at $logDir"
}

if (Test-Path $envPath) {
    Get-Content $envPath | ForEach-Object {
        if ($_ -match "^\s*#") { return }
        if ($_ -match "^\s*$") { return }

        $parts = $_ -split '=', 2
        if ($parts.Length -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()
            [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
    Write-Output "[STEP 5] Environment variables loaded."
} else {
    Write-Output "[STEP 5] ERROR: .env file not found at $envPath"
    exit 1
}

# -------------------------------------
# Step 6: Read Spring profile from env
# -------------------------------------
Write-Output "[STEP 6] Reading profile..."

$profile = [System.Environment]::GetEnvironmentVariable("SPRING_PROFILES_ACTIVE", "Process")
if (-not $profile) {
    Write-Output "[STEP 6] ERROR: SPRING_PROFILES_ACTIVE not set in .env"
    exit 1
}
Write-Output "[STEP 6] Profile: $profile"

# -------------------------------------
# Step 7: Run Spring Boot application
# -------------------------------------
Write-Output "[STEP 7] Starting Spring Boot application..."

try {
    & java "-Dspring.profiles.active=$profile" -jar $jarPath *> "$logDir\application.log"
    Write-Output "[STEP 7] Application started. Logs at $logDir\application.log"
} catch {
    Write-Output "[STEP 7] Failed to start application: $_"
    exit 1
}
