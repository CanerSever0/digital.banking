
package repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.entity.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import repository.TransactionRepository;
import repository.queries.TransactionQueries;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) ->
            Transaction.builder()
                    .id(rs.getLong("id"))
                    .transactionId(rs.getString("transaction_id"))
                    .fromAccountNumber(rs.getString("from_account_number"))
                    .toAccountNumber(rs.getString("to_account_number"))
                    .amount(rs.getBigDecimal("amount"))
                    .transactionType(rs.getString("transaction_type"))
                    .description(rs.getString("description"))
                    .transactionDate(rs.getTimestamp("transaction_date").toLocalDateTime())
                    .status(rs.getString("status"))
                    .build();

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setTransactionDate(LocalDateTime.now());

            jdbcTemplate.update(TransactionQueries.INSERT_TRANSACTION,
                    transaction.getTransactionId(),
                    transaction.getFromAccountNumber(),
                    transaction.getToAccountNumber(),
                    transaction.getAmount(),
                    transaction.getTransactionType(),
                    transaction.getDescription(),
                    transaction.getTransactionDate(),
                    transaction.getStatus()
            );

            log.info("New transaction saved: {}", transaction.getTransactionId());
        }
        return transaction;
    }

    @Override
    public Optional<Transaction> findByTransactionId(String transactionId) {
        try {
            Transaction transaction = jdbcTemplate.queryForObject(
                    TransactionQueries.SELECT_TRANSACTION_BY_TRANSACTION_ID,
                    transactionRowMapper,
                    transactionId
            );
            return Optional.of(transaction);
        } catch (Exception e) {
            log.warn("Transaction not found - Transaction ID: {}", transactionId);
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return jdbcTemplate.query(
                TransactionQueries.SELECT_TRANSACTIONS_BY_ACCOUNT,
                transactionRowMapper,
                accountNumber, accountNumber
        );
    }

    @Override
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcTemplate.query(
                TransactionQueries.SELECT_TRANSACTIONS_BY_DATE_RANGE,
                transactionRowMapper,
                startDate, endDate
        );
    }

    @Override
    public List<Transaction> findByStatus(String status) {
        return jdbcTemplate.query(
                TransactionQueries.SELECT_TRANSACTIONS_BY_STATUS,
                transactionRowMapper,
                status
        );
    }

    @Override
    public boolean updateTransactionStatus(String transactionId, String status) {
        int rowsAffected = jdbcTemplate.update(
                TransactionQueries.UPDATE_TRANSACTION_STATUS,
                status, transactionId
        );

        if (rowsAffected > 0) {
            log.info("Transaction status updated - Transaction: {}, Status: {}", transactionId, status);
            return true;
        }

        log.warn("Transaction status update failed - Transaction: {}", transactionId);
        return false;
    }

    @Override
    public List<Transaction> getAccountTransactionHistory(String accountNumber, int limit) {
        String sql = TransactionQueries.SELECT_TRANSACTIONS_BY_ACCOUNT + " LIMIT " + limit;
        return jdbcTemplate.query(sql, transactionRowMapper, accountNumber, accountNumber);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        try {
            Transaction transaction = jdbcTemplate.queryForObject(
                    TransactionQueries.SELECT_TRANSACTION_BY_ID,
                    transactionRowMapper,
                    id
            );
            return Optional.of(transaction);
        } catch (Exception e) {
            log.warn("Transaction not found - ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> findAll() {
        return jdbcTemplate.query(TransactionQueries.SELECT_ALL_TRANSACTIONS, transactionRowMapper);
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(TransactionQueries.EXISTS_TRANSACTION_BY_ID, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public long count() {
        Integer count = jdbcTemplate.queryForObject(TransactionQueries.COUNT_ALL_TRANSACTIONS, Integer.class);
        return count != null ? count : 0;
    }
}