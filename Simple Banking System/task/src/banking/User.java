package banking;

class User {
    int id;
    String number;
    String pin;

    int balance;

    public User(int id, String number, String pin, int balance) {
        this.id = id;
        this.number = number;
        this.pin = pin;
        this.balance = balance;
    }

    public User(int id, String number, String pin) {
        this.id = id;
        this.number = number;
        this.pin = pin;
        this.balance = 0;
    }
}
