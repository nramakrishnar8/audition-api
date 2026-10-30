# Audition API

The purpose of this Spring Boot application is to test general knowledge of SpringBoot, Java, Gradle etc. It is created for hiring needs of our company but can be used for other purposes.

## Overarching expectations & Assessment areas

<pre>
This is not a university test.
This is meant to be used for job applications and MUST showcase your full skillset.
<b>As such, PRODUCTION-READY code must be written and submitted. </b>
</pre>

- clean, easy to understand code
- good code structures
- Proper code encapsulation
- unit tests with minimum 80% coverage.
- A Working application to be submitted.
- Observability. Does the application contain Logging, Tracing and Metrics instrumentation?
- Input validation.
- Proper error handling.
- Ability to use and configure rest template. We allow for half-setup object mapper and rest template
- Not all information in the Application is perfect. It is expected that a person would figure these out and correct.

## Getting Started

### Prerequisite tooling

- Any Springboot/Java IDE. Ideally IntelliJIdea.
- Java 17
- Gradle 8

### Prerequisite knowledge

- Java
- SpringBoot
- Gradle
- Junit

### Importing Google Java codestyle into INtelliJ

```
- Go to IntelliJ Settings
- Search for "Code Style"
- Click on the "Settings" icon next to the Scheme dropdown
- Choose "Import -> IntelliJ Idea code style XML
- Pick the file "google_java_code_style.xml" from root directory of the application
__Optional__
- Search for "Actions on Save"
    - Check "Reformat Code" and "Organise Imports"
```

---
**NOTE** -
It is  highly recommended that the application be loaded and started up to avoid any issues.

---

## Audition Application information

This section provides information on the application and what needs to be completed as part of the audition application.

The audition consists of multiple TODO statements scattered throughout the codebase. The applicants are expected to:

- Complete all the TODO statements.
- Add unit tests where applicants believe it to be necessary.
- Make sure that all code quality check are completed.
- Gradle build completes sucessfully.
- Make sure the application is functional.

All TODO items in this submission have been completed. The API is a working Spring Boot 3 / Java 17 application that reads posts and comments from JSONPlaceholder, with validation, structured errors, RestTemplate configuration, logging, tracing headers, and Prometheus metrics.

### Verify locally

```powershell
.\gradlew.bat clean check
.\gradlew.bat bootRun
```

Then call:

- `GET http://localhost:8080/posts`
- `GET http://localhost:8080/posts?title=sunt`
- `GET http://localhost:8080/posts/1`
- `GET http://localhost:8080/posts/1/with-comments`
- `GET http://localhost:8080/posts/1/comments`
- `GET http://localhost:8080/actuator/health`
- `GET http://localhost:8080/actuator/prometheus`

Full endpoint examples, configuration, error handling, observability, and testing notes are in [IMPLEMENTATION_NOTES.md](IMPLEMENTATION_NOTES.md).
