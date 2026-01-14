@echo off
REM ----------------------------------------------------------------------------
REM Licensed to the Apache Software Foundation (ASF) under one or more
REM contributor license agreements. See the NOTICE file distributed with
REM this work for additional information regarding copyright ownership.
REM The ASF licenses this file to You under the Apache License, Version 2.0
REM (the "License"); you may not use this file except in compliance
REM with the License. You may obtain a copy of the License at
REM
REM      http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
REM ----------------------------------------------------------------------------

@echo off
REM ----------------------------------------------------------------------------
REM Apache Maven Wrapper - Windows script
REM ----------------------------------------------------------------------------
SETLOCAL

set DIRNAME=%~dp0
set MVNW_HOME=%DIRNAME%\.mvn\wrapper

if defined JAVA_HOME (
	set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
	set JAVA_EXE=java
)

set MAVEN_WRAPPER_JAR=%MVNW_HOME%\maven-wrapper.jar

if exist "%MAVEN_WRAPPER_JAR%" (
	"%JAVA_EXE%" -jar "%MAVEN_WRAPPER_JAR%" %*
) else (
	echo The Maven Wrapper JAR was not found at "%MAVEN_WRAPPER_JAR%".
	echo Run the Maven wrapper generation or download the jar into .mvn\wrapper\
	exit /b 1
)

ENDLOCAL