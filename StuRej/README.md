# Student Course Registration System

A beginner-friendly Java course registration system with both a console workflow and a browser frontend. The backend uses only the Java standard library (`HttpServer`), so no external dependencies are required.

## Project structure

```text
StudentCourseRegistrationSystem/
├── src/
│   ├── Main.java
│   ├── Course.java
│   ├── Student.java
│   ├── CourseDatabase.java
│   ├── StudentDatabase.java
│   └── CourseRegistrationSystem.java
├── web/
│   ├── index.html
│   ├── styles.css
│   └── app.js
└── README.md
```

## Classes and interaction

- `Course` encapsulates course details and seat operations.
- `Student` owns registered courses and handles register/drop rules.
- `CourseDatabase` loads and searches the sample course catalog.
- `StudentDatabase` loads and searches the sample students (`S001` through `S006`).
- `CourseRegistrationSystem` coordinates the console menu and exposes the web API.
- `Main` starts console mode by default or web mode with `--web`.
- The frontend calls the Java API for catalog, registration, and drop operations.

## Compile and run in VS Code

Open the project folder in VS Code, then run these commands from the project root.

### Console application

Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force -Path out
javac -d out src\*.java
java -cp out Main
```

### Browser application

```powershell
New-Item -ItemType Directory -Force -Path out
javac -d out src\*.java
java -cp out Main --web
```

Then open `http://localhost:8080`. Keep the terminal running while using the site. The web mode supports viewing courses, registering, viewing registrations, dropping courses, duplicate checks, full-course checks, and unknown student/course errors.

Requires Java 11 or later because the web server uses `InputStream.readAllBytes()`.

## Manual test cases

1. View all five courses on the console or browser.
2. Register `S001` for `CS101`; a success message appears and seats decrease.
3. Register `S001` for `CS101` again; duplicate registration is rejected.
4. Set a course's capacity to zero in code and try registering; no-seat registration is rejected.
5. View `S001` registrations.
6. Drop `CS101`; a success message appears and seats increase.
7. Drop `CS101` again; the student-not-registered message appears.
8. Enter an unknown student ID; `Student not found.` appears.
9. Enter an unknown course code; `Course not found.` appears.
10. Choose console option 5 to exit with the required messages.
