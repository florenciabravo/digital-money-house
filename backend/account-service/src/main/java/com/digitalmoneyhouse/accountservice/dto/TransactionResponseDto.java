package com.digitalmoneyhouse.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.digitalmoneyhouse.accountservice.entity.Transaction;
import lombok.Data;

@Data
public class TransactionResponseDto {
    private Long id;
    private String type;
    private BigDecimal amount;
    private Long relatedAccountId;
    private String description;
    private LocalDateTime createdAt;

    public TransactionResponseDto(Transaction transaction) {
        this.id = transaction.getId();
        this.type = transaction.getType().name();
        this.amount = transaction.getAmount();
        this.relatedAccountId = transaction.getRelatedAccountId();
        this.description = transaction.getDescription();
        this.createdAt = transaction.getCreatedAt();
    }
}
