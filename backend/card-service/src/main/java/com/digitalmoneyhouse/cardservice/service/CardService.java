package com.digitalmoneyhouse.cardservice.service;

import com.digitalmoneyhouse.cardservice.client.AccountClient;
import com.digitalmoneyhouse.cardservice.dto.CardRequestDto;
import com.digitalmoneyhouse.cardservice.dto.CardResponseDto;
import com.digitalmoneyhouse.cardservice.entity.Card;
import com.digitalmoneyhouse.cardservice.exception.ConflictException;
import com.digitalmoneyhouse.cardservice.exception.ResourceNotFoundException;
import com.digitalmoneyhouse.cardservice.exception.UnauthorizedException;
import com.digitalmoneyhouse.cardservice.repository.CardRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountClient accountClient;

    public CardResponseDto createCard(Long userId, CardRequestDto request) {

        cardRepository.findByNumber(request.getNumber())
                .ifPresent(existing -> {
                    if (existing.getAccountId() != null) {
                        throw new ConflictException("Card already associated to another account");
                    }
                });

        Card card = Card.builder()
                .number(request.getNumber())
                .holderName(request.getHolderName())
                .expiryMonth(request.getExpiryMonth())
                .expiryYear(request.getExpiryYear())
                .type(request.getType())
                .accountId(null)
                .build();

        Card saved = cardRepository.save(card);

        return mapToDto(saved);
    }

    public CardResponseDto associateCardToAccount(Long userId, Long accountId, Long cardId) {

        validateAccountOwnership(accountId, userId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (card.getAccountId() != null) {
            throw new ConflictException("Card already associated");
        }

        card.setAccountId(accountId);
        return mapToDto(cardRepository.save(card));
    }

    public List<CardResponseDto> getCardsByAccount(Long userId, Long accountId) {

        validateAccountOwnership(accountId, userId);

        List<Card> cards = cardRepository.findByAccountId(accountId);

        return cards.stream()
                .map(this::mapToDto)
                .toList();
    }

    public CardResponseDto getCardDetail(Long userId, Long accountId, Long cardId) {

        validateAccountOwnership(accountId, userId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!accountId.equals(card.getAccountId())) {
            throw new ResourceNotFoundException("Card not found for this account");
        }

        return mapToDto(card);
    }

    public void deleteCard(Long userId, Long accountId, Long cardId) {

        validateAccountOwnership(accountId, userId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!accountId.equals(card.getAccountId())) {
            throw new ResourceNotFoundException("Card not found for this account");
        }

        cardRepository.delete(card);
    }

    private void validateAccountOwnership(Long accountId, Long userId) {
        try {
            accountClient.validateAccountOwnership(accountId, userId);

        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Account not found");

        } catch (FeignException.Forbidden e) {
            throw new UnauthorizedException("Do not have permissions for this account");
        }
    }

    private CardResponseDto mapToDto(Card card) {
        return CardResponseDto.builder()
                .id(card.getId())
                .number(card.getNumber())
                .holderName(card.getHolderName())
                .expiryMonth(card.getExpiryMonth())
                .expiryYear(card.getExpiryYear())
                .type(card.getType())
                .accountId(card.getAccountId())
                .build();
    }
}
