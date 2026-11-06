package app.service;

import app.model.Transaction;
import app.model.TransactionType;
import app.repository.TransactionRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FinanceService {
    private final TransactionRepository repository;
    private final List<Transaction> transactions;

    public FinanceService(TransactionRepository repository) {
        this.repository = repository;
        try {
            this.transactions = new ArrayList<>(repository.findAll());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load transaction data: " + ex.getMessage(), ex);
        }
    }

    public Transaction add(TransactionType type, BigDecimal amount, LocalDate date,
                           String category, String remarks) throws IOException {
        Transaction transaction = new Transaction(
            UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            type, amount, date.atStartOfDay(), category, remarks
        );
        transactions.add(transaction);
        repository.saveAll(transactions);
        return transaction;
    }

    public List<Transaction> getAll() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> filter(LocalDate from, LocalDate to, String category) {
        if (from != null && to != null && from.isAfter(to))
            throw new IllegalArgumentException("Start date cannot be after end date.");

        return transactions.stream()
            .filter(t -> from == null || !t.getTimestamp().toLocalDate().isBefore(from))
            .filter(t -> to == null || !t.getTimestamp().toLocalDate().isAfter(to))
            .filter(t -> category == null || category.isBlank()
                || t.getCategory().equalsIgnoreCase(category.trim()))
            .collect(Collectors.toList());
    }

    public MonthlyReport monthlyReport(YearMonth month) {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        Map<String, BigDecimal> byCategory = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            if (!YearMonth.from(t.getTimestamp()).equals(month)) continue;
            if (t.getType() == TransactionType.INCOME) {
                income = income.add(t.getAmount());
            } else {
                expenses = expenses.add(t.getAmount());
                byCategory.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
            }
        }
        return new MonthlyReport(month.toString(), money(income), money(expenses), byCategory);
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
