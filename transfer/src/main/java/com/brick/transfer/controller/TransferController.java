package com.brick.transfer.controller;

import com.brick.transfer.dto.InitiateTransferRequest;
import com.brick.transfer.dto.TransferCallbackRequest;
import com.brick.transfer.dto.ValidateAccountRequest;
import com.brick.transfer.model.Transfer;
import com.brick.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    // 1. Account Validation Endpoint
    @PostMapping("/validate_account")
    public ResponseEntity<Map<String, Object>> validateAccount(@Valid @RequestBody ValidateAccountRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isValid = transferService.validateAccount(request.getAccountNumber(), request.getAccountOwner());
            if (isValid) {
                response.put("status", "success");
                response.put("message", "Account is valid.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "failure");
                response.put("message", "Invalid account details.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error validating account.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 2. Transfer/Disbursement Endpoint
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> initiateTransfer(@Valid @RequestBody InitiateTransferRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Transfer transfer = transferService.initiateTransfer(
                    request.getSourceAccount(),
                    request.getDestinationAccount(),
                    request.getAmount(),
                    request.getCurrency()
            );
            response.put("status", "success");
            response.put("message", "Transfer initiated.");
            response.put("transaction_id", transfer.getTransactionId());
            response.put("status", transfer.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 3. Transfer Callback Endpoint
    @PostMapping("/transfer/callback")
    public ResponseEntity<Map<String, Object>> transferCallback(@Valid @RequestBody TransferCallbackRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            transferService.handleCallback(request.getTransactionId(), request.getStatus());
            response.put("status", "success");
            response.put("message", "Transfer status updated.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating transfer status.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}