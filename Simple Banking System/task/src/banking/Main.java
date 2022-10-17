package banking;

public class Main {
    static String url;
    public static void main(String[] args) {
        url = "jdbc:sqlite:" + args[1];
        DataBase.createDB();
        DataBase.start();
        Interface.start();
//        DataBase.dropDB();
    }
}
