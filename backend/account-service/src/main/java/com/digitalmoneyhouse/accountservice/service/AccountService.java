package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import com.digitalmoneyhouse.accountservice.dto.AccountCreateRequestDto;
import com.digitalmoneyhouse.accountservice.dto.AccountCreateResponseDto;
import com.digitalmoneyhouse.accountservice.entity.Account;
import com.digitalmoneyhouse.accountservice.util.AliasGenerator;
import com.digitalmoneyhouse.accountservice.util.CvuGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CvuGenerator cvuGenerator;
    private final AliasGenerator aliasGenerator;

    public AccountService(AccountRepository accountRepository,
                          CvuGenerator cvuGenerator,
                          AliasGenerator aliasGenerator) {
        this.accountRepository = accountRepository;
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
}
