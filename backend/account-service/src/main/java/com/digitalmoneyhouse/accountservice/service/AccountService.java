package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.*;
import com.digitalmoneyhouse.accountservice.entity.Transaction;
import com.digitalmoneyhouse.accountservice.exception.AliasAlreadyInUseException;
import com.digitalmoneyhouse.accountservice.exception.ResourceNotFoundException;
import com.digitalmoneyhouse.accountservice.exception.UnauthorizedException;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import com.digitalmoneyhouse.accountservice.entity.Account;
import com.digitalmoneyhouse.accountservice.repository.TransactionRepository;
import com.digitalmoneyhouse.accountservice.util.AliasGenerator;
import com.digitalmoneyhouse.accountservice.util.CvuGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CvuGenerator cvuGenerator;
    private final AliasGenerator aliasGenerator;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          CvuGenerator cvuGenerator,
                          AliasGenerator aliasGenerator) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.cvuGenerator = cvuGenerator;
        this.aliasGenerator = aliasGenerator;
    }

    public AccountCreateResponseDto createAccount(AccountCreateRequestDto request) {

        String cvu;
        do {
            cvu = cvuGenerator.generate();
        } while (accountRepository.existsByCvu(cvu));

        String alias;
        do {
            alias = aliasGenerator.generate();
        } while (accountRepository.existsByAlias(alias));

        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setCvu(cvu);
        account.setAlias(alias);
        account.setBalance(BigDecimal.ZERO);

        Account savedAccount = accountRepository.save(account);

        return new AccountCreateResponseDto(
                savedAccount.getId(),
                savedAccount.getCvu(),
                savedAccount.getAlias()
        );
    }

    public AccountBalanceResponseDto getBalance(Long accountId, Long userId) {

        // Search account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Validate that the account belongs to the user
        if (!account.getUserId().equals(userId)) {
            throw new UnauthorizedException("Do not have permissions for this account");
        }

        return new AccountBalanceResponseDto(
                account.getId(),
                account.getBalance()
        );
    }

    public List<TransactionResponseDto> getTransactions(Long accountId, Long userId, Integer limit) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUserId().equals(userId)) {
            throw new UnauthorizedException("Do not have permissions for this account");
        }

        List<Transaction> transactions = (limit != null && limit > 0)
                ? transactionRepository.findTopNByAccountId(accountId, limit)
                : transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);

        return transactions.stream()
                .map(TransactionResponseDto::new)
                .collect(Collectors.toList());
    }

    public AccountProfileDto getByUserId(Long userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for userId: " + userId));
        return new AccountProfileDto(account.getCvu(), account.getAlias());
    }

    public void validateAccountOwnership(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUserId().equals(userId)) {
            throw new UnauthorizedException("Do not have permissions for this account");
        }
    }

    public AccountProfileDto updateAlias(Long accountId, Long userId, AccountUpdateRequestDto request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));

        if (!account.getUserId().equals(userId)) {
            throw new UnauthorizedException("Access denied");
        }

        if (accountRepository.existsByAlias(request.getAlias())) {
            throw new AliasAlreadyInUseException("Alias already in use");
        }

        account.setAlias(request.getAlias());
        accountRepository.save(account);

        return new AccountProfileDto(account.getCvu(), account.getAlias());
    }
}
