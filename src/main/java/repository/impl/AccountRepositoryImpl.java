
package repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.entity.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import repository.AccountRepository;
import repository.queries.AccountQueries;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AccountRepositoryImpl implements AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Account> accountRowMapper = (rs, rowNum) ->
            Account.builder()
                    .id(rs.getLong("id"))
                    .accountNumber(rs.getString("account_number"))
                    .customerId(rs.getString("customer_id"))
                    .balance(rs.getBigDecimal("balance"))
                    .accountType(rs.getString("account_type"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                    .isActive(rs.getBoolean("is_active"))
                    .build();

    @Override
    public Account save(Account account) {
        if (account.getId() == null) {
            LocalDateTime now = LocalDateTime.now();
            account.setCreatedAt(now);
            account.setUpdatedAt(now);

            jdbcTemplate.update(AccountQueries.INSERT_ACCOUNT,
                    account.getAccountNumber(),
                    account.getCustomerId(),
                    account.getBalance(),
                    account.getAccountType(),
                    account.getCreatedAt(),
                    account.getUpdatedAt(),
                    account.getIsActive()
            );

            log.info("New account created: {}", account.getAccountNumber());
        } else {
            update(account);
        }
        return account;
    }

    @Override
    public Optional<Account> findById(Long id) {
        try {
            Account account = jdbcTemplate.queryForObject(AccountQueries.SELECT_ACCOUNT_BY_ID, accountRowMapper, id);
            return Optional.of(account);
        } catch (Exception e) {
            log.warn("Account not found - ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        try {
            Account account = jdbcTemplate.queryForObject(AccountQueries.SELECT_ACCOUNT_BY_NUMBER, accountRowMapper, accountNumber);
            return Optional.of(account);
        } catch (Exception e) {
            log.warn("Account not found - Account Number: {}", accountNumber);
            return Optional.empty();
        }
    }

    @Override
    public List<Account> findByCustomerId(String customerId) {
        return jdbcTemplate.query(AccountQueries.SELECT_ACCOUNTS_BY_CUSTOMER_ID, accountRowMapper, customerId);
    }

    @Override
    public List<Account> findByAccountType(String accountType) {
        return jdbcTemplate.query(AccountQueries.SELECT_ACCOUNTS_BY_TYPE, accountRowMapper, accountType);
    }

    @Override
    public boolean updateBalance(String accountNumber, BigDecimal newBalance) {
        int rowsAffected = jdbcTemplate.update(AccountQueries.UPDATE_ACCOUNT_BALANCE,
                newBalance, LocalDateTime.now(), accountNumber);

        if (rowsAffected > 0) {
            log.info("Account balance updated - Account: {}, New Balance: {}", accountNumber, newBalance);
            return true;
        }

        log.warn("Balance update failed - Account: {}", accountNumber);
        return false;
    }

    @Override
    public List<Account> findActiveAccounts() {
        return jdbcTemplate.query(AccountQueries.SELECT_ACTIVE_ACCOUNTS, accountRowMapper);
    }

    @Override
    public List<Account> findAll() {
        return jdbcTemplate.query(AccountQueries.SELECT_ALL_ACCOUNTS, accountRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(AccountQueries.SOFT_DELETE_ACCOUNT, LocalDateTime.now(), id);
        log.info("Account deactivated - ID: {}", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(AccountQueries.EXISTS_ACCOUNT_BY_ID, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public long count() {
        Integer count = jdbcTemplate.queryForObject(AccountQueries.COUNT_ACTIVE_ACCOUNTS, Integer.class);
        return count != null ? count : 0;
    }

    private void update(Account account) {
        account.setUpdatedAt(LocalDateTime.now());

        jdbcTemplate.update(AccountQueries.UPDATE_ACCOUNT,
                account.getCustomerId(),
                account.getBalance(),
                account.getAccountType(),
                account.getUpdatedAt(),
                account.getIsActive(),
                account.getId()
        );

        log.info("Account updated - ID: {}", account.getId());
    }
}