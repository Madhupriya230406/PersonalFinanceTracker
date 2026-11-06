package app;

import app.model.TransactionType;
import app.repository.TransactionRepository;
import app.service.FinanceService;
import app.service.MonthlyReport;
import app.util.CsvUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class AppVerificationTest {
    private static class InMemoryRepository implements TransactionRepository {
        private final List<app.model.Transaction> data = new ArrayList<>();
        public List<app.model.Transaction> findAll() { return new ArrayList<>(data); }
        public void saveAll(List<app.model.Transaction> items) { data.clear(); data.addAll(items); }
    }

    public static void main(String[] args) throws Exception {
        InMemoryRepository repository = new InMemoryRepository();
        FinanceService service = new FinanceService(repository);

        service.add(TransactionType.INCOME, new BigDecimal("1000.10"),
            LocalDate.parse("2026-10-01"), "Salary", "Monthly salary");
        service.add(TransactionType.EXPENSE, new BigDecimal("100.05"),
            LocalDate.parse("2026-10-02"), "Food", "Groceries");
        service.add(TransactionType.EXPENSE, new BigDecimal("50.00"),
            LocalDate.parse("2026-10-03"), "Food", "Lunch");
        service.add(TransactionType.EXPENSE, new BigDecimal("25.00"),
            LocalDate.parse("2026-11-01"), "Transport", "Bus");

        MonthlyReport report = service.monthlyReport(YearMonth.parse("2026-10"));
        check(report.getIncome().compareTo(new BigDecimal("1000.10")) == 0, "Monthly income");
        check(report.getExpenses().compareTo(new BigDecimal("150.05")) == 0, "Monthly expenses");
        check(report.getNetCashFlow().compareTo(new BigDecimal("850.05")) == 0, "Net cash flow");
        check(report.getCategoryPercentage("Food").compareTo(new BigDecimal("100.00")) == 0,
            "Category percentage");
        check(service.filter(LocalDate.parse("2026-10-01"), LocalDate.parse("2026-10-31"), null).size() == 3,
            "Date range filter");
        check(service.filter(null, null, "food").size() == 2, "Category filter");
        check(CsvUtil.parseLine(CsvUtil.encode("Books, stationery") + ",ok").get(0)
            .equals("Books, stationery"), "CSV escaping");
        System.out.println("[ALL CHECKS PASSED] Personal Finance Tracker requirements verified.");
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError("Failed: " + name);
    }
}
