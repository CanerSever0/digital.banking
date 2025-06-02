package service;

import model.dto.request.CreateAccountRequest;
import model.dto.request.DepositRequest;
import model.dto.request.WithdrawRequest;
import model.dto.response.AccountResponse;
import model.dto.response.BalanceResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse getAccountByNumber(String accountNumber);

    List<AccountResponse> getAccountsByCustomerId(String customerId);

    BalanceResponse getBalance(String accountNumber);

    AccountResponse deposit(DepositRequest request);

    AccountResponse withdraw(WithdrawRequest request);

    void deactivateAccount(String accountNumber);
}