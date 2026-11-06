package app.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class MonthlyReport {
    private final String month;
    private final BigDecimal income;
    private final BigDecimal expenses;
    private final Map<String, BigDecimal> expensesByCategory;

    public MonthlyReport(String month, BigDecimal income, BigDecimal expenses,
                         Map<String, BigDecimal> expensesByCategory) {
        this.month = month;
        this.income = income;
        this.expenses = expenses;
        this.expensesByCategory = new TreeMap<>(expensesByCategory);
    }

    public String getMonth() { return month; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getExpenses() { return expenses; }
    public BigDecimal getNetCashFlow() { return income.subtract(expenses); }
    public Map<String, BigDecimal> getExpensesByCategory() { return new TreeMap<>(expensesByCategory); }

    public BigDecimal getCategoryPercentage(String category) {
        if (expenses.compareTo(BigDecimal.ZERO) == 0) return new BigDecimal("0.00");
        return expensesByCategory.getOrDefault(category, BigDecimal.ZERO)
            .multiply(new BigDecimal("100"))
            .divide(expenses, 2, java.math.RoundingMode.HALF_UP);
    }
}
