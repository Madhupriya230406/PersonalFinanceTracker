package app;

import app.repository.CsvTransactionRepository;
import app.service.FinanceService;
import app.ui.ConsoleApp;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path dataFile = Path.of("data", "transactions.csv");
        Path exportDirectory = Path.of("exports");
        FinanceService service = new FinanceService(new CsvTransactionRepository(dataFile));
        new ConsoleApp(service, exportDirectory).run();
    }
}
