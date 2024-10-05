package com.brick.transfer.external;

import com.brick.transfer.dto.TransferAccountResponse;
import com.brick.transfer.dto.TransferResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BankClientImpl implements BankClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${bank.api.base-url}")
    private String bankApiBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(BankClientImpl.class);

    @Override
    public boolean validateAccount(String accountNumber, String accountOwner) throws Exception {
        logger.info("Validating accountNumber: {}, accountOwner: {}", accountNumber, accountOwner);

        try {
            Mono<ResponseEntity<String>> response = webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/accounts")
                            .queryParam("accountNumber", accountNumber)
                            .queryParam("accountOwner", accountOwner)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new Exception("Error response: " + body)))
                    )
                    .toEntity(String.class);

            ResponseEntity<String> responseEntity = response.block();

            logger.debug("Received Status Code: {}", responseEntity.getStatusCode());
            logger.debug("Received Body: {}", responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String body = responseEntity.getBody();
                TransferAccountResponse[] accounts = objectMapper.readValue(body, TransferAccountResponse[].class);
                logger.info("Number of accounts found: {}", accounts.length);
                return accounts.length > 0;
            } else {
                logger.warn("Received non-OK status: {}", responseEntity.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception during account validation: {}", e.getMessage(), e);
            throw new Exception("Error validating account: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateAccount(String accountNumber) throws Exception {
        logger.info("Validating accountNumber: {}", accountNumber);

        try {
            Mono<ResponseEntity<String>> response = webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/accounts")
                            .queryParam("accountNumber", accountNumber)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new Exception("Error response: " + body)))
                    )
                    .toEntity(String.class);

            ResponseEntity<String> responseEntity = response.block();

            logger.debug("Received Status Code: {}", responseEntity.getStatusCode());
            logger.debug("Received Body: {}", responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String body = responseEntity.getBody();
                TransferAccountResponse[] accounts = objectMapper.readValue(body, TransferAccountResponse[].class);
                logger.info("Number of accounts found: {}", accounts.length);
                return accounts.length > 0;
            } else {
                logger.warn("Received non-OK status: {}", responseEntity.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception during account validation: {}", e.getMessage(), e);
            throw new Exception("Error validating account: " + e.getMessage(), e);
        }
    }


    @Override
    public TransferResponse transferMoney(String sourceAccount, String destinationAccount, Double amount) throws Exception {
        String url = bankApiBaseUrl + "/transfer";

        Map<String, Object> requestBody = Map.of(
                "sourceAccount", sourceAccount,
                "destinationAccount", destinationAccount,
                "amount", amount
        );

        try {
            Mono<ResponseEntity<String>> response = webClientBuilder.build()
                    .post()
                    .uri("/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new Exception("Error response: " + body)))
                    )
                    .toEntity(String.class);

            ResponseEntity<String> responseEntity = response.block();

            if (responseEntity.getStatusCode() == HttpStatus.OK || responseEntity.getStatusCode() == HttpStatus.CREATED) {
                String body = responseEntity.getBody();
                TransferResponse transferResponse = objectMapper.readValue(body, TransferResponse.class);
                return transferResponse;
            } else {
                throw new Exception("Transfer failed with status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            throw new Exception("Error initiating transfer: " + e.getMessage());
        }
    }
}