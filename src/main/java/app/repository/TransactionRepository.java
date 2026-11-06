package app.repository;

import app.model.Transaction;
import java.io.IOException;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> findAll() throws IOException;
    void saveAll(List<Transaction> transactions) throws IOException;
}
