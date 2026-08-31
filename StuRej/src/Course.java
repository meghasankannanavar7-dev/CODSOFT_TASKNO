public class Course {
    private String courseCode;
    private String title;
    private String description;
    private int capacity;
    private String schedule;
    private int availableSeats;

    public Course(String courseCode, String title, String description, int capacity, String schedule) {
        this(courseCode, title, description, capacity, schedule, capacity);
    }

    public Course(String courseCode, String title, String description, int capacity,
                  String schedule, int availableSeats) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.schedule = schedule;
        this.availableSeats = Math.max(0, Math.min(availableSeats, capacity));
    }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = Math.max(0, Math.min(availableSeats, capacity));
    }

    public boolean hasAvailableSeats() { return availableSeats > 0; }
    public void reserveSeat() { if (hasAvailableSeats()) availableSeats--; }
    public void releaseSeat() { if (availableSeats < capacity) availableSeats++; }
}
