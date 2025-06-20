package pl.edu.wat.am.project.zenivalut.model;

import java.time.LocalDate;

public class ObligationData {
    private long id = 0;
    private String obligationTitle;
    private Double obligationAmount;
    private String dateToPay;
    private long categoryId;

    public ObligationData(){}

    public ObligationData(long id, String obligationTitle, Double obligationAmount, String dateToPay, long categoryId) {
        this.id = id;
        this.obligationTitle = obligationTitle;
        this.obligationAmount = obligationAmount;
        this.dateToPay = dateToPay;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObligationTitle() {
        return obligationTitle;
    }

    public void setObligationTitle(String obligationTitle) {
        this.obligationTitle = obligationTitle;
    }

    public Double getObligationAmount() {
        return obligationAmount;
    }

    public void setObligationAmount(Double obligationAmount) {
        this.obligationAmount = obligationAmount;
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
