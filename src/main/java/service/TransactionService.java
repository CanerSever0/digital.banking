package service;

import model.dto.request.TransferRequest;
import model.dto.response.TransactionResponse;
import model.dto.response.TransferResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    TransferResponse transferMoney(TransferRequest request);

    TransactionResponse getTransactionById(String transactionId);

    List<TransactionResponse> getAccountTransactionHistory(String accountNumber, int limit);

    List<TransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<TransactionResponse> getTransactionsByStatus(String status);

    boolean updateTransactionStatus(String transactionId, String status);
}