package controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.dto.request.TransferRequest;
import model.dto.response.ApiResponse;
import model.dto.response.TransactionResponse;
import model.dto.response.TransferResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Management", description = "Transaction operations API")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money", description = "Transfers money between accounts")
    public ResponseEntity<ApiResponse<TransferResponse>> transferMoney(
            @Valid @RequestBody TransferRequest request) {

        log.info("Processing transfer - From: {}, To: {}, Amount: {}",
                request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount());

        try {
            TransferResponse transfer = transactionService.transferMoney(request);
            return ResponseEntity.ok(ApiResponse.success(transfer, "Transfer completed successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Transfer failed - Business error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BUSINESS_ERROR"));
        } catch (RuntimeException e) {
            log.warn("Transfer failed - Account error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "ACCOUNT_ERROR"));
        } catch (Exception e) {
            log.error("Transfer failed - System error: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Transfer failed", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves transaction details by transaction ID")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {

        log.info("Getting transaction - Transaction ID: {}", transactionId);

        try {
            TransactionResponse transaction = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(ApiResponse.success(transaction));
        } catch (RuntimeException e) {
            log.warn("Transaction not found - Transaction ID: {}", transactionId);
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Transaction not found", "TRANSACTION_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting transaction - Transaction ID: {}", transactionId);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve transaction", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get account transaction history", description = "Retrieves transaction history for an account")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAccountTransactionHistory(
            @Parameter(description = "Account number") @PathVariable String accountNumber,
            @Parameter(description = "Limit number of transactions") @RequestParam(defaultValue = "50") int limit) {

        log.info("Getting transaction history - Account: {}, Limit: {}", accountNumber, limit);

        try {
            List<TransactionResponse> transactions = transactionService.getAccountTransactionHistory(accountNumber, limit);
            return ResponseEntity.ok(ApiResponse.success(transactions,
                    transactions.size() + " transactions found"));
        } catch (Exception e) {
            log.error("Error getting transaction history - Account: {}", accountNumber);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve transaction history", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get transactions by date range", description = "Retrieves transactions within a date range")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd HH:mm:ss)")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "End date (yyyy-MM-dd HH:mm:ss)")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        log.info("Getting transactions by date range - Start: {}, End: {}", startDate, endDate);

        try {
            List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(transactions,
                    transactions.size() + " transactions found in date range"));
        } catch (Exception e) {
            log.error("Error getting transactions by date range: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve transactions", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get transactions by status", description = "Retrieves transactions by status")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByStatus(
            @Parameter(description = "Transaction status") @PathVariable String status) {

        log.info("Getting transactions by status: {}", status);

        try {
            List<TransactionResponse> transactions = transactionService.getTransactionsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(transactions,
                    transactions.size() + " transactions found with status: " + status));
        } catch (Exception e) {
            log.error("Error getting transactions by status: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve transactions", "SYSTEM_ERROR"));
        }
    }

    @PutMapping("/{transactionId}/status")
    @Operation(summary = "Update transaction status", description = "Updates the status of a transaction")
    public ResponseEntity<ApiResponse<Void>> updateTransactionStatus(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId,
            @Parameter(description = "New status") @RequestParam String status) {

        log.info("Updating transaction status - Transaction: {}, Status: {}", transactionId, status);

        try {
            boolean updated = transactionService.updateTransactionStatus(transactionId, status);
            if (updated) {
                return ResponseEntity.ok(ApiResponse.success(null, "Transaction status updated successfully"));
            } else {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Transaction not found", "TRANSACTION_NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("Error updating transaction status: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update transaction status", "SYSTEM_ERROR"));
        }
    }
}