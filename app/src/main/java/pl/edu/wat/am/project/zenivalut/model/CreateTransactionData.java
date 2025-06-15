package pl.edu.wat.am.project.zenivalut.model;

public class CreateTransactionData {
    private String transactionTitle;
    private double transactionAmount;
    private String transactionDescription;
    private long categoryId;
    private String transactionType;
    private String transactionDate;

    public CreateTransactionData(String title, double amount, String description, long categoryId, String type, String isoDate) {
        this.transactionTitle = title;
        this.transactionAmount = amount;
        this.transactionDescription = description;
        this.categoryId = categoryId;
        this.transactionType = type;
        this.transactionDate = isoDate;
    }
}
