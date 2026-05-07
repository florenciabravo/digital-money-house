package com.digitalmoneyhouse.cardservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssociateCardRequestDto {

    @NotNull(message = "cardId is required")
    private Long cardId;
}
