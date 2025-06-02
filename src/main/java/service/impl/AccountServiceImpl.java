package service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.dto.request.CreateAccountRequest;
import model.dto.request.DepositRequest;
import model.dto.request.WithdrawRequest;
import model.dto.response.AccountResponse;
import model.dto.response.BalanceResponse;
import model.entity.Account;
import model.entity.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;
import repository.TransactionRepository;
import service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        validateCreateAccountRequest(request);

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .customerId(request.getCustomerId())
                .balance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
                .accountType(request.getAccountType())
                .isActive(true)
                .build();

        Account savedAccount = accountRepository.save(account);

        if (request.getInitialBalance() != null && request.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {
            createDepositTransaction(accountNumber, request.getInitialBalance(), "Initial deposit");
        }

        log.info("New account created - Customer: {}, Account: {}", request.getCustomerId(), accountNumber);

        return mapToAccountResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        return mapToAccountResponse(account);
    }

    @Override
    public List<AccountResponse> getAccountsByCustomerId(String customerId) {
        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BalanceResponse getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        return BalanceResponse.builder()
                .accountNumber(accountNumber)
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .lastUpdated(account.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public AccountResponse deposit(DepositRequest request) {
        validateDepositRequest(request);

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountNumber()));

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        boolean updated = accountRepository.updateBalance(request.getAccountNumber(), newBalance);

        if (!updated) {
            throw new RuntimeException("Balance update failed");
        }

        createDepositTransaction(request.getAccountNumber(), request.getAmount(), request.getDescription());

        Account updatedAccount = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Updated account information could not be retrieved"));

        log.info("Deposit completed - Account: {}, Amount: {}",
                request.getAccountNumber(), request.getAmount());

        return mapToAccountResponse(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponse withdraw(WithdrawRequest request) {
        validateWithdrawRequest(request);

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountNumber()));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance. Current balance: " + account.getBalance());
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        boolean updated = accountRepository.updateBalance(request.getAccountNumber(), newBalance);

        if (!updated) {
            throw new RuntimeException("Balance update failed");
        }

        createWithdrawTransaction(request.getAccountNumber(), request.getAmount(), request.getDescription());

        Account updatedAccount = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Updated account information could not be retrieved"));

        log.info("Withdrawal completed - Account: {}, Amount: {}",
                request.getAccountNumber(), request.getAmount());

        return mapToAccountResponse(updatedAccount);
    }

    @Override
    public void deactivateAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        accountRepository.deleteById(account.getId());
        log.info("Account deactivated: {}", accountNumber);
    }

    private void validateCreateAccountRequest(CreateAccountRequest request) {
        if (request.getCustomerId() == null || request.getCustomerId().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (request.getAccountType() == null || request.getAccountType().trim().isEmpty()) {
            throw new IllegalArgumentException("Account type is required");
        }
        if (request.getInitialBalance() != null && request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
    }

    private void validateDepositRequest(DepositRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    private void validateWithdrawRequest(WithdrawRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
    }

    private String generateAccountNumber() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void createDepositTransaction(String accountNumber, BigDecimal amount, String description) {
        long nowMillis = System.currentTimeMillis();
        String transactionId = "TXN" + nowMillis;
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .toAccountNumber(accountNumber)
                .amount(amount)
                .transactionType("DEPOSIT")
                .description(description)
                .status("COMPLETED")
                .build();

        transactionRepository.save(transaction);
    }

    private void createWithdrawTransaction(String accountNumber, BigDecimal amount, String description) {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .fromAccountNumber(accountNumber)
                .amount(amount)
                .transactionType("WITHDRAW")
                .description(description)
                .status("COMPLETED")
                .build();

        transactionRepository.save(transaction);
    }

    private AccountResponse mapToAccountResponse(Account account) {
        return AccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomerId())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .createdAt(account.getCreatedAt())
                .isActive(account.getIsActive())
                .build();
    }
}