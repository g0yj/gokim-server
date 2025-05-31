# -------------------------------------
# ✅ PowerShell script for production Spring Boot app
# -------------------------------------

# Base path (script directory)
$base = Split-Path -Parent $MyInvocation.MyCommand.Path

# Paths
$envPath = Join-Path $base ".env"
$logDir = Join-Path $base "..\logs"
$jarPath = Join-Path (Join-Path $base "..\target") "gokim-api-1.0.0.jar"

# Create logs directory if not exists
if (-Not (Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir | Out-Null
}

# Load environment variables from .env
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
} else {
    Write-Error "ERROR: .env file not found: $envPath"
    exit 1
}

# Check Spring profile
$profile = [System.Environment]::GetEnvironmentVariable("SPRING_PROFILES_ACTIVE", "Process")
if (-not $profile) {
    Write-Error "ERROR: SPRING_PROFILES_ACTIVE not set in .env"
    exit 1
}

# Run Spring Boot application
Write-Host "Starting application with profile: $profile"
& java "-Dspring.profiles.active=$profile" -jar $jarPath *> "$logDir\application.log"
