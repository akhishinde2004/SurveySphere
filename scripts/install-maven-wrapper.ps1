# Downloads the Maven Wrapper JAR into .mvn/wrapper/
# Usage (PowerShell):
#   ./scripts/install-maven-wrapper.ps1

$ErrorActionPreference = 'Stop'
$wrapperDir = Join-Path -Path $PSScriptRoot -ChildPath '..\.mvn\wrapper' | Resolve-Path -Relative
$wrapperDir = (Resolve-Path (Join-Path $PSScriptRoot '..\.mvn\wrapper')).ProviderPath
if (-not (Test-Path $wrapperDir)) {
    New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null
}

# Takari Maven Wrapper jar (commonly used)
$version = '0.5.6'
$jarName = "maven-wrapper-$version.jar"
$sourceUrl = "https://repo1.maven.org/maven2/io/takari/maven-wrapper/$version/$jarName"
$targetPath = Join-Path $wrapperDir 'maven-wrapper.jar'

Write-Host "Downloading maven-wrapper from $sourceUrl to $targetPath"
Invoke-WebRequest -Uri $sourceUrl -OutFile $targetPath -UseBasicParsing
Write-Host "Downloaded maven-wrapper.jar successfully. You can now run ./mvnw or mvnw.cmd"
