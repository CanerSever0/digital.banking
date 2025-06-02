package repository;

import model.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends BaseReadRepository<Transaction, Long>, BaseWriteRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findByAccountNumber(String accountNumber);

    List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByStatus(String status);

    boolean updateTransactionStatus(String transactionId, String status);

    List<Transaction> getAccountTransactionHistory(String accountNumber, int limit);
}