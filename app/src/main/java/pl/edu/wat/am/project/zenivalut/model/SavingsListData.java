package pl.edu.wat.am.project.zenivalut.model;

import java.sql.Timestamp;

public class SavingsListData {
    private long id;
    private String title;
    private double currentAmount;
    private double finalAmmount;
    private String deadline;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public double getFinalAmmount() {
        return finalAmmount;
    }

    public void setFinalAmmount(double finalAmmount) {
        this.finalAmmount = finalAmmount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
