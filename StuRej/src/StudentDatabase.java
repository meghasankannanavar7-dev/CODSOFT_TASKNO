import java.util.ArrayList;
import java.util.List;

public class StudentDatabase {
    private final ArrayList<Student> students = new ArrayList<>();

    public StudentDatabase() {
        students.add(new Student("S001", "Aisha Khan"));
        students.add(new Student("S002", "Daniel Lee"));
        students.add(new Student("S003", "Maya Patel"));
        students.add(new Student("S004", "Ethan Williams"));
        students.add(new Student("S005", "Sofia Garcia"));
        students.add(new Student("S006", "Noah Johnson"));
    }

    public List<Student> getStudents() { return new ArrayList<>(students); }
    public Student findStudent(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equalsIgnoreCase(studentId)) return student;
        }
        return null;
    }
}
