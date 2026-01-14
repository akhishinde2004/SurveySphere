# Online Survey System

Simple Spring Boot survey application with server-side templates.

Key points

- Java + Spring Boot 3.1.5
- Thymeleaf templates with Spring Security integration
- H2 in-memory DB for local development (see `src/main/resources/application.properties`)
- Passwords hashed with BCrypt

Quick start

Requirements: Java 17+, Maven

Build and run:

```bash
mvn clean package
java -jar target/online-survey-system-1.0.0.jar
```

Maven Wrapper

You can generate the Maven Wrapper (mvnw) locally if you prefer using the wrapper:

```bash
mvn -N org.apache.maven.plugins:maven-wrapper:3.1.1:wrapper
```

This will produce the `.mvn/wrapper/maven-wrapper.jar` used by the `mvnw` scripts. The repository includes `mvnw`/`mvnw.cmd` and `maven-wrapper.properties`, but the wrapper JAR may need to be generated locally.

Automatic helper (Windows PowerShell)

If you don't want to run the Maven wrapper plugin, there's a helper script that downloads a commonly used Maven wrapper JAR into `.mvn/wrapper/`:

```powershell
./scripts/install-maven-wrapper.ps1
```

This will place `maven-wrapper.jar` in `.mvn/wrapper/` so `mvnw`/`mvnw.cmd` work out of the box.

Dev tips

- Default dev profile uses H2 in-memory DB. Use `application-mysql.properties` and `--spring.profiles.active=mysql` to run against MySQL.
- Access H2 console at `/h2-console` (enabled in dev).

Security

- Form-based login powered by Spring Security. CSRF tokens are enabled and added to forms.
- Passwords are stored using BCrypt.

CI / Docker

- The repository contains GitHub Actions workflows under `.github/workflows/` for build and Docker image builds.
- There's also a sample `Dockerfile` to containerize the app.

Contributing

- Run tests: `mvn test`
- Follow the TODO list in the repo to see remaining improvements (security hardening, tests, CI publishing).

