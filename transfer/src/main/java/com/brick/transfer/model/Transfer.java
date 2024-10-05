package com.brick.transfer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Column(name="source_account", nullable = false)
    private String sourceAccount;

    @Column(name="destination_account", nullable = false)
    private String destinationAccount;

    @Column(name="amount", nullable = false)
    private Double amount;

    @Column(name="currency", nullable = false)
    private String currency;

    @Column(name="status", nullable = false)
    private String status;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
