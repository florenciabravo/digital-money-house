package com.digitalmoneyhouse.cardservice.controller;

import com.digitalmoneyhouse.cardservice.dto.AssociateCardRequestDto;
import com.digitalmoneyhouse.cardservice.dto.CardRequestDto;
import com.digitalmoneyhouse.cardservice.dto.CardResponseDto;
import com.digitalmoneyhouse.cardservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponseDto> createCard(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid CardRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardService.createCard(userId, request));
    }

    @PostMapping("/accounts/{accountId}/cards")
    public ResponseEntity<CardResponseDto> associateCard(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId,
            @RequestBody AssociateCardRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardService.associateCardToAccount(userId, accountId, request.getCardId()));
    }

    @GetMapping("/accounts/{accountId}/cards")
    public ResponseEntity<List<CardResponseDto>> getCardsByAccount(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId) {

        return ResponseEntity.ok(cardService.getCardsByAccount(userId, accountId));
    }

    @GetMapping("/accounts/{accountId}/cards/{cardId}")
    public ResponseEntity<CardResponseDto> getCardDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId,
            @PathVariable Long cardId) {

        return ResponseEntity.ok(cardService.getCardDetail(userId, accountId, cardId));
    }

    @DeleteMapping("/accounts/{accountId}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId,
            @PathVariable Long cardId) {

        cardService.deleteCard(userId, accountId, cardId);
        return ResponseEntity.ok().build();
    }
}
