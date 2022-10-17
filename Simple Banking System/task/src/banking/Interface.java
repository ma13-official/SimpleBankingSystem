package banking;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

class Interface {
    static Scanner sc = new Scanner(System.in);
    static User curUser;

    public static void start() {
        curUser = null;
        System.out.println("""
                1. Create an account
                2. Log into account
                0. Exit""");
        inputStart();
    }

    public static void inputStart() {
        switch (sc.nextInt()) {
            case (1) -> createAnAccount();
            case (2) -> login();
            case (0) -> exit();
        }
        sc.nextLine();
    }


    private static void createAnAccount() {
        curUser = new User(DataBase.numberOfUsers, DataBase.nextCard, makeRandomPin());
        DataBase.addUser(curUser);
        printAfterCreateAnAccount();
        start();
    }

    private static void printAfterCreateAnAccount() {
        System.out.println("""
                Your card has been created
                Your card number: \n""" + curUser.number +
                "\nYour card PIN:\n" + curUser.pin);
    }

    private static String makeRandomPin() {
        StringBuilder newPin = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            newPin.append(random.nextInt(9));
        }
        return String.valueOf(newPin);
    }

    private static void login() {
        System.out.println("Enter your card number:");
        String inCardNumber = sc.next();
        System.out.println("Enter your PIN:");
        String inPin = sc.next();
        boolean checker = true;
        for (User user : DataBase.currentDB()) {
            if (Objects.equals(user.pin, inPin) && Objects.equals(user.number, inCardNumber)) {
                curUser = user;
                System.out.println("You have successfully logged in!");
                checker = false;
                printAfterLogin();
            }
        }
        if (checker) System.out.println("Wrong card number or PIN!"); start();
    }

    private static void printAfterLogin() {
        System.out.println("""
                1. Balance
                2. Add income
                3. Do transfer
                4. Close account
                5. Log out
                0. Exit""");
        DataBase.updateDB(curUser);
        inputAfterLogin();
    }

    private static void transfer() {
        System.out.println("Enter card number:");
        String curNumber = sc.next();
        if (Objects.equals(curNumber, curUser.number)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!(DataBase.checkLuhn(curNumber))) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (!(checkIfCardExists(curNumber))) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int transferMoney = sc.nextInt();
            if (transferMoney > curUser.balance) {
                System.out.println("Not enough money!");
            } else {
                User incomeUser = userIfCardExists(curNumber);
                incomeUser.balance += transferMoney;
                curUser.balance -= transferMoney;
                DataBase.updateDB(incomeUser);
                DataBase.updateDB(curUser);
                System.out.println("Success!");
            }
        }
    }

    private static boolean checkIfCardExists(String number) {
        for (User user : DataBase.currentDB()) {
            if (Objects.equals(user.number, number)) {
                return true;
            }
        }
        return false;
    }

    private static User userIfCardExists(String number) {
        for (User user : DataBase.currentDB()) {
            if (Objects.equals(user.number, number)) {
                return user;
            }
        }
        return null;
    }

    private static void inputAfterLogin() {
        switch (sc.nextInt()) {
            case (1) -> {
                System.out.println("Balance: " + curUser.balance);
                printAfterLogin();
            }
            case (2) -> {
                System.out.println("Enter income:");
                curUser.balance += sc.nextInt();
                printAfterLogin();
            }
            case (3) -> {
                transfer();
                printAfterLogin();
            }
            case (4) -> {
                DataBase.dropInDB(curUser);
                System.out.println("The account has been closed!");
                DataBase.updateDB(curUser);
                start();
            }
            case (5) -> {
                System.out.println("You have successfully logged out!");
                DataBase.updateDB(curUser);
                start();
            }
            case (0) -> exit();
        }
        sc.nextLine();
    }

    private static void exit(){
        System.out.print("Bye!");
        DataBase.showDB();
        System.exit(0);
    }

}
