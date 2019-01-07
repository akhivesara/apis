package main.webapp;

public class DataUpdatingTool {
    public static void main(String[] args) throws Exception {
        // package as a jar with dependencies

        // maven plugin to package jar with dependency copied

        // manifest by pointing the main class
//        String dbUrl = args[0];
//        String user = args[1];
//        String pwd = args[2];

        DatabaseController.getInstance().fetchAndSaveRatings();
    }
}
