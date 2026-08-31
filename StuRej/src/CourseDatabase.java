import java.util.ArrayList;
import java.util.List;

public class CourseDatabase {
    private final ArrayList<Course> courses = new ArrayList<>();

    public CourseDatabase() { loadSampleCourses(); }

    private void loadSampleCourses() {
        courses.add(new Course("CS101", "Java Programming", "Introduction to Java", 50, "Monday 10:00 AM"));
        courses.add(new Course("CS102", "Data Structures", "Algorithms and core data structures", 40, "Tuesday 11:00 AM"));
        courses.add(new Course("CS103", "Database Management System", "Relational databases and SQL", 35, "Wednesday 1:00 PM"));
        courses.add(new Course("CS104", "Web Development", "Modern frontend and backend development", 30, "Thursday 2:00 PM"));
        courses.add(new Course("CS105", "Computer Networks", "Networking principles and protocols", 45, "Friday 9:00 AM"));
    }

    public List<Course> getCourses() { return new ArrayList<>(courses); }
    public Course findCourse(String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode().equalsIgnoreCase(courseCode)) return course;
        }
        return null;
    }
}
