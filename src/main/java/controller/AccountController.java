package controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.dto.request.CreateAccountRequest;
import model.dto.request.DepositRequest;
import model.dto.request.WithdrawRequest;
import model.dto.response.AccountResponse;
import model.dto.response.ApiResponse;
import model.dto.response.BalanceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Management", description = "Bank account operations API")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create new account", description = "Creates a new bank account for a customer")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        log.info("Creating new account - Customer ID: {}, Type: {}",
                request.getCustomerId(), request.getAccountType());

        try {
            AccountResponse account = accountService.createAccount(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(account, "Account created successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Account creation failed - Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Account creation failed - System error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Account creation failed", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by number", description = "Retrieves account information by account number")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(
            @Parameter(description = "Account number") @PathVariable String accountNumber) {

        log.info("Getting account - Account Number: {}", accountNumber);

        try {
            AccountResponse account = accountService.getAccountByNumber(accountNumber);
            return ResponseEntity.ok(ApiResponse.success(account));
        } catch (RuntimeException e) {
            log.warn("Account not found - Account Number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Account not found", "ACCOUNT_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting account - Account Number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve account", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get accounts by customer ID", description = "Retrieves all accounts for a specific customer")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsByCustomerId(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {

        log.info("Getting accounts for customer - Customer ID: {}", customerId);

        try {
            List<AccountResponse> accounts = accountService.getAccountsByCustomerId(customerId);
            return ResponseEntity.ok(ApiResponse.success(accounts,
                    accounts.size() + " accounts found for customer"));
        } catch (Exception e) {
            log.error("Error getting accounts for customer - Customer ID: {}", customerId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve accounts", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get account balance", description = "Retrieves current balance for an account")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(
            @Parameter(description = "Account number") @PathVariable String accountNumber) {

        log.info("Getting balance - Account Number: {}", accountNumber);

        try {
            BalanceResponse balance = accountService.getBalance(accountNumber);
            return ResponseEntity.ok(ApiResponse.success(balance));
        } catch (RuntimeException e) {
            log.warn("Account not found for balance - Account Number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Account not found", "ACCOUNT_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting balance - Account Number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve balance", "SYSTEM_ERROR"));
        }
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit money", description = "Deposits money into an account")
    public ResponseEntity<ApiResponse<AccountResponse>> deposit(
            @Valid @RequestBody DepositRequest request) {

        log.info("Processing deposit - Account: {}, Amount: {}",
                request.getAccountNumber(), request.getAmount());

        try {
            AccountResponse account = accountService.deposit(request);
            return ResponseEntity.ok(ApiResponse.success(account, "Deposit completed successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Deposit failed - Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (RuntimeException e) {
            log.warn("Deposit failed - Account not found: {}", request.getAccountNumber());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Account not found", "ACCOUNT_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Deposit failed - System error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Deposit failed", "SYSTEM_ERROR"));
        }
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraws money from an account")
    public ResponseEntity<ApiResponse<AccountResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request) {

        log.info("Processing withdrawal - Account: {}, Amount: {}",
                request.getAccountNumber(), request.getAmount());

        try {
            AccountResponse account = accountService.withdraw(request);
            return ResponseEntity.ok(ApiResponse.success(account, "Withdrawal completed successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Withdrawal failed - Validation/Business error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BUSINESS_ERROR"));
        } catch (RuntimeException e) {
            log.warn("Withdrawal failed - Account not found: {}", request.getAccountNumber());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Account not found", "ACCOUNT_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Withdrawal failed - System error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Withdrawal failed", "SYSTEM_ERROR"));
        }
    }

    @DeleteMapping("/{accountNumber}")
    @Operation(summary = "Deactivate account", description = "Deactivates a bank account (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @Parameter(description = "Account number") @PathVariable String accountNumber) {

        log.info("Deactivating account - Account Number: {}", accountNumber);

        try {
            accountService.deactivateAccount(accountNumber);
            return ResponseEntity.ok(ApiResponse.success(null, "Account deactivated successfully"));
        } catch (RuntimeException e) {
            log.warn("Account deactivation failed - Account not found: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Account not found", "ACCOUNT_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Account deactivation failed - System error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Account deactivation failed", "SYSTEM_ERROR"));
        }
    }
}