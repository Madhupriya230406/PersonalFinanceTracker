package app.repository;

import app.model.Transaction;
import app.model.TransactionType;
import app.util.CsvUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvTransactionRepository implements TransactionRepository {
    private static final String HEADER = "id,type,amount,timestamp,category,remarks";
    private final Path file;

    public CsvTransactionRepository(Path file) {
        this.file = file;
    }

    @Override
    public List<Transaction> findAll() throws IOException {
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        if (Files.notExists(file)) {
            Files.writeString(file, HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
            return new ArrayList<>();
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            try {
                List<String> f = CsvUtil.parseLine(lines.get(i));
                if (f.size() != 6) throw new IllegalArgumentException("Expected 6 fields.");
                transactions.add(new Transaction(
                    f.get(0), TransactionType.valueOf(f.get(1)),
                    new BigDecimal(f.get(2)), LocalDateTime.parse(f.get(3)),
                    f.get(4), f.get(5)
                ));
            } catch (RuntimeException ex) {
                throw new IOException("Invalid transaction data at CSV line " + (i + 1) + ": " + ex.getMessage(), ex);
            }
        }
        return transactions;
    }

    @Override
    public void saveAll(List<Transaction> transactions) throws IOException {
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Transaction t : transactions) {
            lines.add(String.join(",",
                CsvUtil.encode(t.getId()),
                CsvUtil.encode(t.getType().name()),
                t.getAmount().toPlainString(),
                CsvUtil.encode(t.getTimestamp().toString()),
                CsvUtil.encode(t.getCategory()),
                CsvUtil.encode(t.getRemarks())
            ));
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}
