import java.util.ArrayList;
import java.util.List;

public class Student {
    private String studentId;
    private String studentName;
    private final ArrayList<Course> registeredCourses;

    public Student(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.registeredCourses = new ArrayList<>();
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public List<Course> getRegisteredCourses() { return new ArrayList<>(registeredCourses); }

    public boolean isRegistered(String courseCode) {
        for (Course course : registeredCourses) {
            if (course.getCourseCode().equalsIgnoreCase(courseCode)) return true;
        }
        return false;
    }

    public boolean registerCourse(Course course) {
        if (course == null || isRegistered(course.getCourseCode()) || !course.hasAvailableSeats()) return false;
        registeredCourses.add(course);
        course.reserveSeat();
        return true;
    }

    public boolean dropCourse(String courseCode) {
        for (int index = 0; index < registeredCourses.size(); index++) {
            Course course = registeredCourses.get(index);
            if (course.getCourseCode().equalsIgnoreCase(courseCode)) {
                registeredCourses.remove(index);
                course.releaseSeat();
                return true;
            }
        }
        return false;
    }

    public void displayRegisteredCourses() {
        if (registeredCourses.isEmpty()) {
            System.out.println("No courses registered.");
            return;
        }
        for (Course course : registeredCourses) {
            System.out.printf("%s - %s (%s)%n", course.getCourseCode(), course.getTitle(), course.getSchedule());
        }
    }
}
