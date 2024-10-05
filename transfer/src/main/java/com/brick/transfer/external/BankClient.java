package com.brick.transfer.external;

import com.brick.transfer.dto.TransferResponse;

public interface BankClient {
    boolean validateAccount(String accountNumber, String accountOwner) throws Exception;
    boolean validateAccount(String accountNumber) throws Exception;
    TransferResponse transferMoney(String sourceAccount, String destinationAccount, Double amount) throws Exception;
}