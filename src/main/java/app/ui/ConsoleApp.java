package app.ui;

import app.model.Transaction;
import app.model.TransactionType;
import app.service.FinanceService;
import app.service.MonthlyReport;
import app.util.CsvUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleApp {
    private final FinanceService service;
    private final Path exportDirectory;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(FinanceService service, Path exportDirectory) {
        this.service = service;
        this.exportDirectory = exportDirectory;
    }

    public void run() {
        System.out.println("====================================");
        System.out.println("       PERSONAL FINANCE TRACKER");
        System.out.println("====================================");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = prompt("Choose an option: ");
            try {
                switch (choice) {
                    case "1" -> addTransaction(TransactionType.INCOME);
                    case "2" -> addTransaction(TransactionType.EXPENSE);
                    case "3" -> showTransactions(service.getAll());
                    case "4" -> filterTransactions();
                    case "5" -> showMonthlyReport();
                    case "6" -> exportMonthlyReport();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option. Please choose 0 to 6.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        System.out.println("Application closed. Your transactions are saved.");
    }

    private void printMenu() {
        System.out.println("\n1. Add income");
        System.out.println("2. Add expense");
        System.out.println("3. View all transactions");
        System.out.println("4. Filter transactions");
        System.out.println("5. View monthly summary");
        System.out.println("6. Export monthly summary to CSV");
        System.out.println("0. Exit");
    }

    private void addTransaction(TransactionType type) throws IOException {
        BigDecimal amount = new BigDecimal(prompt("Amount: ").trim());
        LocalDate date = LocalDate.parse(prompt("Date (yyyy-MM-dd): ").trim());
        String category = prompt("Category: ");
        String remarks = prompt("Remarks: ");
        Transaction saved = service.add(type, amount, date, category, remarks);
        System.out.println(type + " saved successfully. ID: " + saved.getId());
    }

    private void filterTransactions() {
        String fromText = prompt("From date (yyyy-MM-dd, blank for none): ").trim();
        String toText = prompt("To date (yyyy-MM-dd, blank for none): ").trim();
        String category = prompt("Category (blank for all): ").trim();
        LocalDate from = fromText.isEmpty() ? null : LocalDate.parse(fromText);
        LocalDate to = toText.isEmpty() ? null : LocalDate.parse(toText);
        showTransactions(service.filter(from, to, category));
    }

    private void showTransactions(List<Transaction> list) {
        if (list.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.printf("%-10s %-9s %-12s %-20s %-16s %s%n",
            "ID", "TYPE", "AMOUNT", "TIMESTAMP", "CATEGORY", "REMARKS");
        for (Transaction t : list) {
            System.out.printf("%-10s %-9s %-12s %-20s %-16s %s%n",
                t.getId(), t.getType(), t.getAmount().toPlainString(),
                t.getTimestamp(), t.getCategory(), t.getRemarks());
        }
    }

    private void showMonthlyReport() {
        YearMonth month = readMonth();
        printReport(service.monthlyReport(month));
    }

    private void printReport(MonthlyReport report) {
        System.out.println("\nMonthly Report: " + report.getMonth());
        System.out.println("Total income   : " + report.getIncome());
        System.out.println("Total expenses : " + report.getExpenses());
        System.out.println("Net cash flow  : " + report.getNetCashFlow());
        System.out.println("\nCategory-wise spending:");
        if (report.getExpensesByCategory().isEmpty()) {
            System.out.println("No expenses recorded for this month.");
        } else {
            for (Map.Entry<String, BigDecimal> e : report.getExpensesByCategory().entrySet()) {
                System.out.printf("%-18s %10s  %6s%%%n", e.getKey(), e.getValue(),
                    report.getCategoryPercentage(e.getKey()).toPlainString());
            }
        }
    }

    private void exportMonthlyReport() throws IOException {
        YearMonth month = readMonth();
        MonthlyReport report = service.monthlyReport(month);
        Files.createDirectories(exportDirectory);
        Path output = exportDirectory.resolve("monthly_report_" + month + ".csv");

        List<String> lines = new ArrayList<>();
        lines.add("Report Month," + CsvUtil.encode(report.getMonth()));
        lines.add("Monthly Income," + report.getIncome().toPlainString());
        lines.add("Monthly Expenses," + report.getExpenses().toPlainString());
        lines.add("Net Cash Flow," + report.getNetCashFlow().toPlainString());
        lines.add("");
        lines.add("Expense Category,Amount,Spending Percentage");
        for (Map.Entry<String, BigDecimal> e : report.getExpensesByCategory().entrySet()) {
            lines.add(CsvUtil.encode(e.getKey()) + "," + e.getValue().toPlainString()
                + "," + report.getCategoryPercentage(e.getKey()).toPlainString());
        }
        Files.write(output, lines, StandardCharsets.UTF_8);
        System.out.println("CSV report exported: " + output.toAbsolutePath());
    }

    private YearMonth readMonth() {
        String value = prompt("Month (yyyy-MM): ").trim();
        return YearMonth.parse(value);
    }

    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
