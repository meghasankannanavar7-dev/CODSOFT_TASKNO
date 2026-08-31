import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseRegistrationSystem {
    private final CourseDatabase courseDatabase;
    private final StudentDatabase studentDatabase;

    public CourseRegistrationSystem() {
        courseDatabase = new CourseDatabase();
        studentDatabase = new StudentDatabase();
    }

    public void runConsole() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("System initialized. Sample courses and students loaded.");
        while (running) {
            displayMenu();
            int choice = readMenuChoice(scanner);
            switch (choice) {
                case 1: displayAvailableCourses(); break;
                case 2: registerFromConsole(scanner); break;
                case 3: displayRegisteredFromConsole(scanner); break;
                case 4: dropFromConsole(scanner); break;
                case 5:
                    System.out.println("Thank you for using Student Course Registration System.");
                    System.out.println("Exiting the system...");
                    running = false;
                    break;
                default: System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n========================================");
        System.out.println(" STUDENT COURSE REGISTRATION SYSTEM");
        System.out.println("========================================");
        System.out.println("1. View Available Courses");
        System.out.println("2. Register for a Course");
        System.out.println("3. View My Registered Courses");
        System.out.println("4. Drop a Course");
        System.out.println("5. Exit");
        System.out.println("========================================");
    }

    private int readMenuChoice(Scanner scanner) {
        String value = scanner.nextLine().trim();
        try { return Integer.parseInt(value); }
        catch (NumberFormatException exception) { return -1; }
    }

    private String readRequired(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Input cannot be empty.");
        }
    }

    private void displayAvailableCourses() {
        System.out.println("\nAvailable Courses");
        for (Course course : courseDatabase.getCourses()) printCourse(course);
    }

    private void printCourse(Course course) {
        System.out.printf("\nCourse Code: %s%nTitle: %s%nDescription: %s%nCapacity: %d%nSchedule: %s%nAvailable Seats: %d%n",
                course.getCourseCode(), course.getTitle(), course.getDescription(), course.getCapacity(),
                course.getSchedule(), course.getAvailableSeats());
    }

    private void registerFromConsole(Scanner scanner) {
        Student student = studentDatabase.findStudent(readRequired(scanner, "Enter Student ID: "));
        if (student == null) { System.out.println("Student not found."); return; }
        Course course = courseDatabase.findCourse(readRequired(scanner, "Enter Course Code: "));
        System.out.println(register(student, course));
    }

    private void displayRegisteredFromConsole(Scanner scanner) {
        Student student = studentDatabase.findStudent(readRequired(scanner, "Enter Student ID: "));
        if (student == null) { System.out.println("Student not found."); return; }
        System.out.println("\nRegistered courses for " + student.getStudentName() + ":");
        student.displayRegisteredCourses();
    }

    private void dropFromConsole(Scanner scanner) {
        Student student = studentDatabase.findStudent(readRequired(scanner, "Enter Student ID: "));
        if (student == null) { System.out.println("Student not found."); return; }
        String courseCode = readRequired(scanner, "Enter Course Code: ");
        System.out.println(drop(student, courseCode));
    }

    private String register(Student student, Course course) {
        if (course == null) return "Course not found.";
        if (student.isRegistered(course.getCourseCode())) return "You are already registered for this course.";
        if (!course.hasAvailableSeats()) return "Registration failed: No seats available.";
        student.registerCourse(course);
        return "Course registered successfully!";
    }

    private String drop(Student student, String courseCode) {
        return student.dropCourse(courseCode)
                ? "Course dropped successfully!"
                : "You are not registered for this course.";
    }

    public void startWebServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/courses", this::handleCourses);
        server.createContext("/api/register", this::handleRegister);
        server.createContext("/api/drop", this::handleDrop);
        server.createContext("/api/students", this::handleStudentCourses);
        server.createContext("/", this::handleStaticFile);
        server.setExecutor(null);
        server.start();
        System.out.println("Web application running at http://localhost:" + port);
    }

    private void handleCourses(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) { send(exchange, 405, "Method not allowed"); return; }
        StringBuilder json = new StringBuilder("[");
        for (Course course : courseDatabase.getCourses()) {
            if (json.length() > 1) json.append(',');
            json.append(courseJson(course));
        }
        sendJson(exchange, 200, json.append(']').toString());
    }

    private void handleStudentCourses(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        if (parts.length < 5 || !"GET".equals(exchange.getRequestMethod())) { send(exchange, 400, "Invalid student request"); return; }
        Student student = studentDatabase.findStudent(parts[3]);
        if (student == null) { send(exchange, 404, "Student not found."); return; }
        StringBuilder json = new StringBuilder("[");
        for (Course course : student.getRegisteredCourses()) {
            if (json.length() > 1) json.append(',');
            json.append(courseJson(course));
        }
        sendJson(exchange, 200, json.append(']').toString());
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) { send(exchange, 405, "Method not allowed"); return; }
        String body = readBody(exchange);
        String studentId = jsonValue(body, "studentId");
        String courseCode = jsonValue(body, "courseCode");
        Student student = studentDatabase.findStudent(studentId);
        if (student == null) { send(exchange, 404, "Student not found."); return; }
        send(exchange, 200, register(student, courseDatabase.findCourse(courseCode)));
    }

    private void handleDrop(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) { send(exchange, 405, "Method not allowed"); return; }
        String body = readBody(exchange);
        Student student = studentDatabase.findStudent(jsonValue(body, "studentId"));
        if (student == null) { send(exchange, 404, "Student not found."); return; }
        send(exchange, 200, drop(student, jsonValue(body, "courseCode")));
    }

    private String courseJson(Course course) {
        return String.format("{\"courseCode\":\"%s\",\"title\":\"%s\",\"description\":\"%s\",\"capacity\":%d,\"schedule\":\"%s\",\"availableSeats\":%d}",
                escape(course.getCourseCode()), escape(course.getTitle()), escape(course.getDescription()), course.getCapacity(),
                escape(course.getSchedule()), course.getAvailableSeats());
    }

    private String jsonValue(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(json);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) { return new String(input.readAllBytes(), StandardCharsets.UTF_8); }
    }

    private String escape(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\""); }

    private void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        send(exchange, status, body);
    }

    private void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) { output.write(bytes); }
    }

    private void handleStaticFile(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String requested = uri.getPath().equals("/") ? "/index.html" : uri.getPath();
        Path root = Paths.get("web").toAbsolutePath().normalize();
        Path file = root.resolve(requested.substring(1)).normalize();
        if (!file.startsWith(root) || !Files.exists(file) || Files.isDirectory(file)) { send(exchange, 404, "Not found"); return; }
        byte[] bytes = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType(file));
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) { output.write(bytes); }
    }

    private String contentType(Path file) {
        String name = file.getFileName().toString();
        if (name.endsWith(".html")) return "text/html; charset=UTF-8";
        if (name.endsWith(".css")) return "text/css; charset=UTF-8";
        return "application/javascript; charset=UTF-8";
    }
}
