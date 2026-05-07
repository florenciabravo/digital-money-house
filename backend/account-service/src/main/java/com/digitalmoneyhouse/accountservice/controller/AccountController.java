package com.digitalmoneyhouse.accountservice.controller;

import com.digitalmoneyhouse.accountservice.dto.*;
import com.digitalmoneyhouse.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountCreateResponseDto> createAccount(
            @Valid @RequestBody AccountCreateRequestDto request) {

        AccountCreateResponseDto response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountBalanceResponseDto> getBalance(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(accountService.getBalance(id, userId));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer limit) {

        return ResponseEntity.ok(accountService.getTransactions(id, userId, limit));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountProfileDto> getAccountByUserId(
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long requestingUserId) {

        if (!userId.equals(requestingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(accountService.getByUserId(userId));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Void> validateAccountOwnership(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {

        accountService.validateAccountOwnership(id, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountProfileDto> updateAlias(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AccountUpdateRequestDto request) {

        return ResponseEntity.ok(accountService.updateAlias(id, userId, request));
    }
}
