package pl.edu.wat.am.project.zenivalut.model;

public class BalanceData {
    private Double balance;
    private Double euroBalance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getEuroBalance() {
        return euroBalance;
    }

    public void setEuroBalance(Double euroBalance) {
        this.euroBalance = euroBalance;
    }

    public BalanceData(Double balance, Double euroBalance) {
        this.balance = balance;
        this.euroBalance = euroBalance;
    }
}
