package pl.edu.wat.am.project.zenivalut.model;

import java.time.LocalDate;

public class CreateObligationData {
    private String title;

    private Double amount;

    private String dateToPay;

    private long categoryId;

    public CreateObligationData(){}

    public CreateObligationData(String title, Double amount, String dateToPay, long categoryId) {
        this.title = title;
        this.amount = amount;
        this.dateToPay = dateToPay;
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDateToPay() {
        return dateToPay;
    }

    public void setDateToPay(String dateToPay) {
        this.dateToPay = dateToPay;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
