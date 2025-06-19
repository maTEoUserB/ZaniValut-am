package pl.edu.wat.am.project.zenivalut.model;

public class DailyExpensesDTO {
    private String dateLabel;
    private Double totalAmount;


    public DailyExpensesDTO(String dateLabel, Double totalAmount) {
        this.dateLabel = dateLabel;
        this.totalAmount = totalAmount;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
