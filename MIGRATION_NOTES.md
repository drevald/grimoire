# Spring Boot 3 + Gradle Migration Notes

## Overview
Successfully migrated the Grimoire project from:
- **Spring Framework 5.3.19** → **Spring Boot 3.4.1** (includes Spring 6.x)
- **Maven** → **Gradle 8.11.1**
- **Java 11** → **Java 17**
- **Tomcat WAR deployment** → **Embedded Tomcat JAR**
- **XML Configuration** → **Java-based Configuration**
- **javax.*** → **jakarta.*** namespace
- **Log4j** → **SLF4J with Logback**

## Key Changes

### Build System
- Created `build.gradle` and `settings.gradle`
- Initialized Gradle wrapper (8.11.1)
- Configured to build executable JAR with embedded Tomcat
- Updated to use Spring Boot Gradle plugin

### Java Configuration Classes Created
1. **GrimoireApplication.java** - Main Spring Boot application class
   - Location: `src/main/java/org/helico/GrimoireApplication.java`
   - Enables async and scheduling support

2. **WebConfig.java** - Web MVC configuration
   - Location: `src/main/java/org/helico/config/WebConfig.java`
   - Configures JSP view resolver
   - Handles static resources

3. **SecurityConfig.java** - Security configuration
   - Location: `src/main/java/org/helico/config/SecurityConfig.java`
   - Uses SecurityFilterChain (Spring Security 6.x style)
   - Maintains JDBC authentication with existing database schema
   - **WARNING**: Still using NoOpPasswordEncoder (insecure for production)

### Application Properties
- Created `src/main/resources/application.properties`
- Configured datasource with environment variables
- Set up JPA/Hibernate properties
- Configured multipart file upload limits
- JSP view configuration
- Logging levels

### Removed Files
- `src/main/webapp/WEB-INF/web.xml`
- `src/main/resources/root-context.xml`
- `src/main/resources/security.xml`
- `src/main/resources/data.xml`
- `src/main/resources/appServlet/servlet-context.xml`
- `src/main/resources/appServlet/controllers.xml`
- `src/main/resources/hibernate.cfg.xml`
- `src/main/resources/log4j.properties`

### Code Updates
- All `javax.persistence.*` → `jakarta.persistence.*`
- All `javax.servlet.*` → `jakarta.servlet.*`
- All `org.apache.log4j.Logger` → `org.slf4j.Logger`
- Fixed SLF4J logging calls (e.g., `LOG.error(e, e)` → `LOG.error("Error occurred", e)`)
- Updated `StartupInitializer` to use `ApplicationReadyEvent` instead of `ContextRefreshedEvent`

### Docker Updates
- **Dockerfile**: Now uses Gradle build and Java 17
  - Build stage: `gradle:8.11.1-jdk17`
  - Runtime stage: `eclipse-temurin:17-jre`
  - Builds executable JAR instead of WAR
  - Updated startup script for JAR execution

- **startup.sh**: Updated for Spring Boot JAR
  - Runs with `java -jar` instead of Tomcat catalina
  - Maintains debug port 8000
  - Respects PORT environment variable

### Configuration
- **gradle.properties**: Points to Java 17 at `C:\jdk-17`

## How to Build

### Using Gradle Wrapper (Recommended)
```bash
# Windows
gradlew.bat clean build

# Linux/Mac
./gradlew clean build
```

### Build without tests
```bash
gradlew.bat clean build -x test
```

### Run locally

#### Quick Start (Recommended)
```bash
# 1. Copy .env.example to .env
copy .env.example .env

# 2. Edit .env with your database credentials

# 3. Run using the helper script
# Windows (CMD):
run-local.bat

# Windows (Git Bash) / Linux / Mac:
./run-local.sh
```

#### Using .env file manually
```bash
# Windows - Load environment variables from .env
for /f "usebackq tokens=1,* delims==" %a in (".env") do if not "%a"=="" if not "%a:~0,1%"=="#" set %a=%b

# Linux/Mac - Load environment variables from .env
export $(grep -v '^#' .env | xargs)

# Then run with Gradle
./gradlew bootRun
```

#### Manual environment setup
```bash
# Set environment variables manually (Windows)
set DATABASE_JNDI=jdbc:postgresql://localhost:5434/grimoire
set DATABASE_USER=denis
set DATABASE_PASSWORD=9rommit
set LOCAL_STORAGE=C:\temp\GRIM

# Run the JAR
java -jar build/libs/grimoire.jar
```

### Docker Compose
```bash
docker-compose up --build
```

## Build Artifacts
- **Executable JAR**: `build/libs/grimoire.jar` (~66MB)
- **Plain JAR**: `build/libs/grimoire-1.0-SNAPSHOT-plain.jar` (classes only)

## Environment Variables

**For local development**, use the `.env` file (see `.env.example` for template):
```bash
# Copy the example file and edit with your values
copy .env.example .env
```

Required variables:
- `DATABASE_JNDI` - PostgreSQL connection URL (e.g., `jdbc:postgresql://localhost:5432/grimoire`)
- `DATABASE_USER` - Database username
- `DATABASE_PASSWORD` - Database password
- `LOCAL_STORAGE` - Local file storage path (e.g., `C:\temp\GRIM`)

Optional variables:
- `PORT` - Server port (default: 8081, configured in application.properties)

## Post-Migration Fixes

### JPA Entity ID Generation Strategy
**Issue**: After migration to Hibernate 6.x (Spring Boot 3), entities with `@GeneratedValue` without an explicit strategy caused errors like:
```
ERROR: relation "text_seq" does not exist
```

**Root Cause**:
- Database schema uses PostgreSQL `bigserial` (auto-increment)
- Entities had `@GeneratedValue` without strategy specification
- Hibernate 6.x changed default sequence naming, looking for `<table>_seq` instead of using `bigserial`

**Fix Applied**: Updated all entities to use `GenerationType.IDENTITY`:
- Text.java
- Job.java
- Account.java
- AccountLang.java
- DictWord.java
- Translation.java
- Translator.java
- TranslatorProvider.java
- Word.java

Changed from:
```java
@GeneratedValue
```

To:
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

This properly instructs Hibernate to use PostgreSQL's `bigserial` auto-increment mechanism.

## Known Issues & Recommendations

### Security
1. **NoOpPasswordEncoder** is deprecated and insecure
   - Consider migrating to BCryptPasswordEncoder
   - Will require password hash migration in database

2. **Old commons-httpclient 3.1**
   - This library is deprecated
   - Consider migrating to Apache HttpClient 5.x or Spring WebClient

### Testing
- Tests were skipped during build (`-x test`)
- Test classes may need updates for Spring Boot 3 compatibility
- Consider adding Spring Boot Test configuration

### JSP Support
- JSP is supported but not recommended for new Spring Boot applications
- Consider migrating to Thymeleaf or React/Vue frontend

## Next Steps
1. Test the application thoroughly with database
2. Update tests for Spring Boot 3
3. Consider migrating to BCrypt password encoding
4. Update deprecated dependencies (commons-httpclient)
5. Review and update any custom security configurations
6. Consider adding Spring Boot Actuator for monitoring

## Compatibility Notes
- Minimum Java version: 17
- Spring Boot: 3.4.1
- Spring Security: 6.x (included in Spring Boot)
- Hibernate: 6.x (included in Spring Boot)
- PostgreSQL driver: 42.7.2
- Gradle: 8.11.1

## Rollback Strategy
The original pom.xml and XML configuration files can be restored from git history if needed. The Maven build should still work if you checkout to a previous commit.
