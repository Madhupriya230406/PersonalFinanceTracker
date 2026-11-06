package app.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private final String id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String category;
    private final String remarks;

    public Transaction(String id, TransactionType type, BigDecimal amount,
                       LocalDateTime timestamp, String category, String remarks) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Transaction ID is required.");
        this.type = Objects.requireNonNull(type, "Transaction type is required.");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp is required.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Category is required.");

        this.id = id.trim();
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.category = category.trim();
        this.remarks = remarks == null ? "" : remarks.trim();
    }

    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getCategory() { return category; }
    public String getRemarks() { return remarks; }
}
