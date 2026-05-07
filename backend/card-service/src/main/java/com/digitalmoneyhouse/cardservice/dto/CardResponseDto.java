package com.digitalmoneyhouse.cardservice.dto;

import com.digitalmoneyhouse.cardservice.entity.Card;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardResponseDto {
    private Long id;
    private String number;
    private String holderName;
    private Integer expiryMonth;
    private Integer expiryYear;
    private Card.CardType type;
    private Long accountId;
}
