package com.digitalmoneyhouse.cardservice.dto;

import com.digitalmoneyhouse.cardservice.entity.Card;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDto {

    @NotBlank(message = "The card number is required")
    @Pattern(
            regexp = "\\d{16}",
            message = "Card number must contain exactly 16 digits"
    )
    private String number;

    @NotBlank(message = "The cardholder's name is required")
    private String holderName;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer expiryMonth;

    @NotNull
    private Integer expiryYear;

    @NotNull(message = "Card type required (Debit or Credit)")
    private Card.CardType type;
}
