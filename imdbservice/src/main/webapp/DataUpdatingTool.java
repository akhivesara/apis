package main.webapp;

public class DataUpdatingTool {
    public static void main(String[] args) throws Exception {
        String dbUrl = args[0];
        String user = args[1];
        String pwd = args[2];

        MySQLStore db = MySQLStore.getInstance(dbUrl, user, pwd);
        //db.testInsert(args[3] + "", "Title Name " + args[3]);
    }
}
