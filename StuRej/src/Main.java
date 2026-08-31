public class Main {
    public static void main(String[] args) throws Exception {
        CourseRegistrationSystem system = new CourseRegistrationSystem();
        if (args.length > 0 && "--web".equalsIgnoreCase(args[0])) {
            system.startWebServer(8080);
            Thread.currentThread().join();
        } else {
            system.runConsole();
        }
    }
}
