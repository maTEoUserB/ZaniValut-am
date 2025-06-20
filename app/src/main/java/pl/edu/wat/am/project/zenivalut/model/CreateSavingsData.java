package pl.edu.wat.am.project.zenivalut.model;

import java.time.LocalDate;

public class CreateSavingsData {
    private String title;
    private double currentAmount;
    private double finalAmount;
    private String deadline;

    public CreateSavingsData(){}
    public CreateSavingsData(String title, double currentAmount, double finalAmount, String deadline) {
        this.title = title;
        this.currentAmount = currentAmount;
        this.finalAmount = finalAmount;
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }
}
