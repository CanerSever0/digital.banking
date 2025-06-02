package repository;

import model.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends BaseReadRepository<Account, Long>,
        BaseWriteRepository<Account, Long>,
        DeletableRepository<Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(String customerId);

    List<Account> findByAccountType(String accountType);

    boolean updateBalance(String accountNumber, BigDecimal newBalance);

    List<Account> findActiveAccounts();
}