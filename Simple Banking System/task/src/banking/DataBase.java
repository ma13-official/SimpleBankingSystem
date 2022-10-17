package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class DataBase {
    static int numberOfUsers = 1;
    static String nextCard = "4000000000000002";
    static int nextEndOfCard = 2;
    static SQLiteDataSource dataSource = new SQLiteDataSource();

    static void start() {
        ArrayList<User> curUsers = DataBase.currentDB();
        DataBase.numberOfUsers = curUsers.size() + 1;
        if (DataBase.numberOfUsers != 1) {
            DataBase.nextCard = curUsers.get(curUsers.size()-1).number;
            StringBuilder nextEndOfCard = new StringBuilder();
            boolean checker = true;
            for (int i = 6; i < 16; i++) {
                if (checker) {
                    if (!(Objects.equals(DataBase.nextCard.charAt(i), '0'))) {
                        checker = false;
                        nextEndOfCard.append(DataBase.nextCard.charAt(i));
                    }
                } else nextEndOfCard.append(DataBase.nextCard.charAt(i));
            }
            DataBase.nextEndOfCard = Integer.parseInt(nextEndOfCard.toString());
        }
    }

    static void createDB() {
        dataSource.setUrl(Main.url);
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "     id INTEGER PRIMARY KEY,\n"
                + "     number TEXT,\n"
                + "     pin TEXT,\n"
                + "     balance INTEGER DEFAULT 0"
                + ");";
        execute(sql);
    }

    static void dropDB() {
        String sql = "DELETE FROM card";
        execute(sql);
    }


    static void writeInDB(int id, String card, String pin, int balance) {
        String sql = String.format("INSERT INTO card " +
                "VALUES (%d, '%s', '%s', %d)", id, card, pin, balance);
        execute(sql);
    }

    static void dropInDB(User user) {
        String sql = String.format("DELETE FROM card WHERE id=%d;", user.id);
        execute(sql);
    }

    static void updateDB(User user) {
        String sql = String.format("UPDATE card \n" +
                "SET \n" +
                "    number = '%s', \n" +
                "    pin = '%s', \n" +
                "    balance = %d \n" +
                "WHERE \n" +
                "    id = %d;", user.number, user.pin, user.balance, user.id);
        execute(sql);
    }

    private static void execute(String sql) {
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static ArrayList<User> currentDB() {
        ResultSet rs;
        ArrayList<User> out = new ArrayList<User>();
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                rs = statement.executeQuery("SELECT * FROM card");
                while (rs.next()) {
                    User cur = new User(rs.getInt("id"), rs.getString("number"),
                            rs.getString("pin"), rs.getInt("balance"));
                    out.add(cur);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    static ArrayList<User> showDB() {
        ResultSet rs;
        ArrayList<User> out = new ArrayList<User>();
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                rs = statement.executeQuery("SELECT * FROM card");
                while (rs.next()) {
                    System.out.println((rs.getInt("id") + " " + rs.getString("number") + " " +
                             rs.getString("pin") + " " + rs.getInt("balance")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }
    static void addUser(User newUser) {
        numberOfUsers++;
        do {
            nextEndOfCard++;
            changeNextCard();
        } while (!checkLuhn(nextCard));
        writeInDB(newUser.id, newUser.number, newUser.pin, newUser.balance);
    }

    private static void changeNextCard() {
        StringBuilder newNextCard = new StringBuilder("4");
        for (int i = 0; i < 15 - Math.ceil(Math.log10(nextEndOfCard)); i++) {
            newNextCard.append(0);
        }
        newNextCard.append(nextEndOfCard);
        nextCard = String.valueOf(newNextCard);
    }

    static boolean checkLuhn(String number) {
        char[] nextCardList = number.toCharArray();
        int luhnSum = 0;
        for (int i = 0; i < 15; i++) {
            int cur = Character.getNumericValue(nextCardList[i]);
            if (i % 2 == 0) cur *= 2;
            if (cur > 9) cur -= 9;
            luhnSum += cur;
        }
        return luhnSum % 10 == 10 - Character.getNumericValue(nextCardList[15]);
    }
}
