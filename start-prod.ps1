# -------------------------------------
# ✅ Step -1: Kill running Java process
# -------------------------------------
Write-Host "Killing existing Java process (if any)..."
Get-Process java -ErrorAction SilentlyContinue | ForEach-Object {
    try {
        $_ | Stop-Process -Force
        Write-Host "Java process stopped (PID: $($_.Id))"
    } catch {
        Write-Warning "Failed to stop Java process: $_"
    }
}

# -------------------------------------
# ✅ PowerShell script for production Spring Boot app
# -------------------------------------

# Base path (script directory)
$base = Split-Path -Parent $MyInvocation.MyCommand.Path

# -------------------------------------
# ✅ Step 0: Jenkins에서 빌드된 최신 JAR 복사
# -------------------------------------
# Jenkins 빌드 아티팩트 위치 (실제 경로로 수정)
$jenkinsJar = "C:\ProgramData\Jenkins\.jenkins\workspace\deploy-backend\target\gokim-api-1.0.0.jar"

# 복사할 위치 (운영 디렉토리 target)
$deployJar = "C:\app\backend\target\gokim-api-1.0.0.jar"

# 최신 빌드된 JAR 복사 (기존 파일 덮어씀)
Copy-Item -Path $jenkinsJar -Destination $deployJar -Force
# 최신 스크립트 복사
Copy-Item "C:\ProgramData\Jenkins\.jenkins\workspace\deploy-backend\start-prod.ps1" `
          -Destination "C:\app\backend\deploy\start-prod.ps1" -Force
# -------------------------------------
# ✅ Step 1: 환경 설정 및 로그 경로 지정
# -------------------------------------
$envPath = Join-Path $base ".env"
$logDir = Join-Path $base "..\logs"
$jarPath = $deployJar  # 바로 위에서 복사한 경로 그대로 사용

# Create logs directory if not exists
if (-Not (Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir | Out-Null
}

# -------------------------------------
# ✅ Step 2: .env 파일에서 환경 변수 로드
# -------------------------------------
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

# -------------------------------------
# ✅ Step 3: 프로파일 확인
# -------------------------------------
$profile = [System.Environment]::GetEnvironmentVariable("SPRING_PROFILES_ACTIVE", "Process")
if (-not $profile) {
    Write-Error "ERROR: SPRING_PROFILES_ACTIVE not set in .env"
    exit 1
}

# -------------------------------------
# ✅ Step 4: Spring Boot 실행
# -------------------------------------
Write-Host "Starting application with profile: $profile"
& java "-Dspring.profiles.active=$profile" -jar $jarPath *> "$logDir\application.log"
