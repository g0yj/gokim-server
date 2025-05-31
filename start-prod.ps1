# 현재 스크립트 경로
$base = Split-Path -Parent $MyInvocation.MyCommand.Path

# .env 불러오기
Get-Content "$base\.env" | ForEach-Object {
    if ($_ -match "^\s*#") { return }
    if ($_ -match "^\s*$") { return }
    $parts = $_ -split '=', 2
    if ($parts.Length -eq 2) {
        [System.Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
    }
}

# jar 경로 (상위 target 폴더 기준)
$jarPath = Join-Path (Join-Path $base "..\target") "gokim-api-1.0.0.jar"

# 프로필 확인
$profile = [System.Environment]::GetEnvironmentVariable("SPRING_PROFILES_ACTIVE", "Process")
if (-not $profile) {
    Write-Error "Not Found .env"
    exit 1
}

# 실행
Write-Host "Application run... 프로필: $profile"
java "-Dspring.profiles.active=$profile" -jar $jarPath

