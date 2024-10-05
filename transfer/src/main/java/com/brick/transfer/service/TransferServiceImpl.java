package com.brick.transfer.service;


import com.brick.transfer.dto.TransferResponse;
import com.brick.transfer.external.BankClient;
import com.brick.transfer.model.Transfer;
import com.brick.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final BankClient bankClient;

    @Override
    public boolean validateAccount(String accountNumber, String accountOwner) throws Exception {
        return bankClient.validateAccount(accountNumber, accountOwner);
    }

    @Override
    @Transactional
    public Transfer initiateTransfer(String sourceAccount, String destinationAccount, Double amount, String currency) throws Exception {
        // Validate source account
        boolean isSourceValid = bankClient.validateAccount(sourceAccount); // Replace with actual logic if available
        if (!isSourceValid) {
            throw new Exception("Invalid source account.");
        }

        // Validate destination account
        boolean isDestinationValid = bankClient.validateAccount(destinationAccount); // Replace with actual logic if available
        if (!isDestinationValid) {
            throw new Exception("Invalid destination account.");
        }

        // Initiate transfer
        TransferResponse transaction = bankClient.transferMoney(sourceAccount, destinationAccount, amount);

        // Create Transfer record
        Transfer transfer = Transfer.builder()
                .transactionId(transaction.getId().toString())
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .amount(amount)
                .currency("IDR") // Assuming USD; adjust as needed
                .status("pending")
                .createdAt(LocalDateTime.now())
                .build();

        return transferRepository.save(transfer);
    }

    @Override
    @Transactional
    public void handleCallback(String transactionId, String status) throws Exception {
        Transfer transfer = transferRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new Exception("Transfer not found with Transaction ID: " + transactionId));

        transfer.setStatus(status);
        transfer.setUpdatedAt(LocalDateTime.now());

        transferRepository.save(transfer);
    }
}