package com.brick.transfer.service;

import com.brick.transfer.model.Transfer;

public interface TransferService {
    boolean validateAccount(String accountNumber, String accountOwner) throws Exception;
    Transfer initiateTransfer(String sourceAccount, String destinationAccount, Double amount, String currency) throws Exception;
    void handleCallback(String transactionId, String status) throws Exception;
}