# Personal Finance Tracker — CodeNova Task 02

A Java 17 console application for recording income and expenses, calculating monthly financial summaries, filtering transaction history, and exporting reports to CSV.

## Requirements covered

- Records income and expenditure with timestamp, category, and remarks.
- Calculates monthly income, expenses, net cash flow, and category-wise spending percentages.
- Filters transactions by date range and/or category.
- Exports a structured monthly financial summary to CSV.
- Uses `BigDecimal` for currency calculations, `java.time` for dates, and Java NIO.2 for file storage.
- Includes sample transaction data and a sample exported report.

## Technology

- Java 17 (compatible with Java 11+)
- Java Collections Framework
- `java.math.BigDecimal`
- `java.time`
- `java.nio.file` (NIO.2)
- CSV file storage; no external libraries or paid services

## Project structure

```text
PersonalFinanceTracker/
├── data/
│   └── transactions.csv
├── exports/
│   └── sample_monthly_report.csv
├── src/
│   ├── main/java/app/Main.java
│   ├── main/java/app/model/Transaction.java
│   ├── main/java/app/model/TransactionType.java
│   ├── main/java/app/repository/CsvTransactionRepository.java
│   ├── main/java/app/repository/TransactionRepository.java
│   ├── main/java/app/service/FinanceService.java
│   ├── main/java/app/service/MonthlyReport.java
│   ├── main/java/app/ui/ConsoleApp.java
│   └── main/java/app/util/CsvUtil.java
├── src/test/java/app/AppVerificationTest.java
├── run.bat
└── README.md
```

## Run on Windows (VS Code terminal)

1. Install JDK 11 or later and make sure `java` and `javac` are available in PATH.
2. Open this project folder in VS Code.
3. Run:

```bat
run.bat
```

The application creates `data/transactions.csv` if it does not exist. The sample data is included.

## Compile and run manually

```bat
dir /s /b src\main\java\*.java > sources.txt
javac -d out @sources.txt
java -cp out app.Main
```

## Menu

1. Add income
2. Add expense
3. View all transactions
4. Filter transactions by date range/category
5. View monthly summary
6. Export monthly summary to CSV
0. Exit

For a date range, enter dates in `yyyy-MM-dd` format. Amounts must be positive decimal values. Use a category such as Salary, Food, Transport, Education, Bills, Shopping, or Other. Categories are not restricted to this sample list.

## Financial calculation approach

- **Monthly income** = sum of all income transactions in the selected calendar month.
- **Monthly expenses** = sum of all expense transactions in the selected calendar month.
- **Net cash flow** = monthly income − monthly expenses.
- **Category spending percentage** = (expense total for a category ÷ total monthly expenses) × 100.
- All monetary values use `BigDecimal` with scale 2 and `RoundingMode.HALF_UP`; `double` is not used for currency calculations.
- A zero-expense month reports category percentages as 0.00%, avoiding division by zero.

## Data and report files

- Transactions are stored persistently in `data/transactions.csv`.
- Exported monthly reports are written to `exports/monthly_report_YYYY-MM.csv`.
- The included `exports/sample_monthly_report.csv` is an example report for October 2026.

## Verification

Compile the application, then compile and run the verification test:

```bat
dir /s /b src\main\java\*.java > sources.txt
javac -d out @sources.txt
javac -cp out -d out src\test\java\app\AppVerificationTest.java
java -cp out app.AppVerificationTest
```

The test checks monthly totals, net cash flow, category percentages, date/category filters, and CSV escaping.
