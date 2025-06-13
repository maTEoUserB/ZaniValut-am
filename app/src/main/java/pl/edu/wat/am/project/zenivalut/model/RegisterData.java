package pl.edu.wat.am.project.zenivalut.model;

public class RegisterData {
    private String username;
    private String password;
    private Double balance;

    public RegisterData(String exampleUser, String number, double balance) {
        this.username = exampleUser;
        this.password = number;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
