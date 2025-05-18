# -------------------------------
# ✅ 운영용 PowerShell 실행 스크립트
# -------------------------------

# .env 파일에서 환경변수 불러오기
Get-Content ".env" | ForEach-Object {
    if ($_ -match "^\s*#") { return }         # 주석 건너뛰기
    if ($_ -match "^\s*$") { return }         # 빈 줄 건너뛰기

    $parts = $_ -split '=', 2
    if ($parts.Length -eq 2) {
        $name = $parts[0].Trim()
        $value = $parts[1].Trim()
        [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
    }
}

# 실행할 JAR 경로
$jarName = "target/gokim-api-1.0.0.jar"

# 프로필 확인
$profile = [System.Environment]::GetEnvironmentVariable("SPRING_PROFILES_ACTIVE", "Process")
if (-not $profile) {
    Write-Error "❌ SPRING_PROFILES_ACTIVE가 .env에 설정되지 않았습니다."
    exit 1
}

# 로그 출력 및 실행
Write-Host "🚀 애플리케이션 실행 중... 프로필: $profile"
java "-Dspring.profiles.active=$profile" -jar $jarName
