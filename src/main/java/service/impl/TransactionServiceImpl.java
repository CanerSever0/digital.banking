package service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.dto.request.TransferRequest;
import model.dto.response.TransactionResponse;
import model.dto.response.TransferResponse;
import model.entity.Account;
import model.entity.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;
import repository.TransactionRepository;
import service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public TransferResponse transferMoney(TransferRequest request) {
        validateTransferRequest(request);

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Source account not found: " + request.getFromAccountNumber()));

        if (!fromAccount.getIsActive()) {
            throw new IllegalArgumentException("Source account is not active");
        }

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found: " + request.getToAccountNumber()));

        if (!toAccount.getIsActive()) {
            throw new IllegalArgumentException("Destination account is not active");
        }

        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Available: " + fromAccount.getBalance() +
                    ", Required: " + request.getAmount());
        }

        String transactionId = generateTransactionId();

        try {
            BigDecimal newFromBalance = fromAccount.getBalance().subtract(request.getAmount());
            accountRepository.updateBalance(fromAccount.getAccountNumber(), newFromBalance);

            BigDecimal newToBalance = toAccount.getBalance().add(request.getAmount());
            accountRepository.updateBalance(toAccount.getAccountNumber(), newToBalance);

            Transaction transaction = Transaction.builder()
                    .transactionId(transactionId)
                    .fromAccountNumber(request.getFromAccountNumber())
                    .toAccountNumber(request.getToAccountNumber())
                    .amount(request.getAmount())
                    .transactionType("TRANSFER")
                    .description(request.getDescription() != null ? request.getDescription() : "Money Transfer")
                    .status("COMPLETED")
                    .build();

            Transaction savedTransaction = transactionRepository.save(transaction);

            log.info("Transfer completed - Transaction ID: {}, From: {}, To: {}, Amount: {}",
                    transactionId, request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount());

            return TransferResponse.builder()
                    .transactionId(transactionId)
                    .fromAccountNumber(request.getFromAccountNumber())
                    .toAccountNumber(request.getToAccountNumber())
                    .amount(request.getAmount())
                    .description(transaction.getDescription())
                    .transactionDate(savedTransaction.getTransactionDate())
                    .status("COMPLETED")
                    .fromAccountBalance(newFromBalance)
                    .toAccountBalance(newToBalance)
                    .build();

        } catch (Exception e) {
            log.error("Transfer failed - Transaction ID: {}, Error: {}", transactionId, e.getMessage());

            Transaction failedTransaction = Transaction.builder()
                    .transactionId(transactionId)
                    .fromAccountNumber(request.getFromAccountNumber())
                    .toAccountNumber(request.getToAccountNumber())
                    .amount(request.getAmount())
                    .transactionType("TRANSFER")
                    .description("FAILED: " + e.getMessage())
                    .status("FAILED")
                    .build();

            transactionRepository.save(failedTransaction);

            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }
    }

    @Override
    public TransactionResponse getTransactionById(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        return mapToTransactionResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAccountTransactionHistory(String accountNumber, int limit) {
        accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        return transactionRepository.getAccountTransactionHistory(accountNumber, limit)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return transactionRepository.findByDateRange(startDate, endDate)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getTransactionsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status +
                    ". Valid statuses: PENDING, COMPLETED, FAILED, CANCELLED");
        }

        return transactionRepository.findByStatus(status.toUpperCase())
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateTransactionStatus(String transactionId, String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        return transactionRepository.updateTransactionStatus(transactionId, status.toUpperCase());
    }

    private void validateTransferRequest(TransferRequest request) {
        if (request.getFromAccountNumber() == null || request.getFromAccountNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Source account number is required");
        }

        if (request.getToAccountNumber() == null || request.getToAccountNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination account number is required");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        BigDecimal maxTransferLimit = new BigDecimal("1000000");
        if (request.getAmount().compareTo(maxTransferLimit) > 0) {
            throw new IllegalArgumentException("Transfer amount exceeds maximum limit: " + maxTransferLimit);
        }

        BigDecimal minTransferLimit = new BigDecimal("1");
        if (request.getAmount().compareTo(minTransferLimit) < 0) {
            throw new IllegalArgumentException("Transfer amount below minimum limit: " + minTransferLimit);
        }
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean isValidStatus(String status) {
        return List.of("PENDING", "COMPLETED", "FAILED", "CANCELLED")
                .contains(status.toUpperCase());
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .status(transaction.getStatus())
                .build();
    }
}