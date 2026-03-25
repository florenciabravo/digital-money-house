package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.AccountCreateRequestDto;
import com.digitalmoneyhouse.accountservice.dto.AccountCreateResponseDto;
import com.digitalmoneyhouse.accountservice.entity.Account;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import com.digitalmoneyhouse.accountservice.util.AliasGenerator;
import com.digitalmoneyhouse.accountservice.util.CvuGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CvuGenerator cvuGenerator;

    @Mock
    private AliasGenerator aliasGenerator;

    @InjectMocks
    private AccountService accountService;

    // Create account successful
    @Test
    void shouldCreateAccountSuccessfully() {

        AccountCreateRequestDto request = new AccountCreateRequestDto();
        request.setUserId(1L);

        when(cvuGenerator.generate()).thenReturn("1234567890123456789012");
        when(aliasGenerator.generate()).thenReturn("luna.sol.rio");

        when(accountRepository.existsByCvu(anyString())).thenReturn(false);
        when(accountRepository.existsByAlias(anyString())).thenReturn(false);

        Account savedAccount = new Account();
        savedAccount.setId(10L);
        savedAccount.setUserId(1L);
        savedAccount.setCvu("1234567890123456789012");
        savedAccount.setAlias("luna.sol.rio");
        savedAccount.setBalance(BigDecimal.ZERO);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountCreateResponseDto response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals(10L, response.getAccountId());
        assertEquals("1234567890123456789012", response.getCvu());
        assertEquals("luna.sol.rio", response.getAlias());

        verify(accountRepository).save(any(Account.class));
    }

    // Duplicate CVU
    @Test
    void shouldGenerateAnotherCvuIfAlreadyExists() {

        AccountCreateRequestDto request = new AccountCreateRequestDto();
        request.setUserId(1L);

        when(cvuGenerator.generate())
                .thenReturn("1111111111111111111111")
                .thenReturn("2222222222222222222222");

        when(aliasGenerator.generate()).thenReturn("luna.sol.rio");

        when(accountRepository.existsByCvu("1111111111111111111111")).thenReturn(true);
        when(accountRepository.existsByCvu("2222222222222222222222")).thenReturn(false);

        when(accountRepository.existsByAlias(anyString())).thenReturn(false);

        Account savedAccount = new Account();
        savedAccount.setId(20L);
        savedAccount.setCvu("2222222222222222222222");
        savedAccount.setAlias("luna.sol.rio");

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountCreateResponseDto response = accountService.createAccount(request);

        assertEquals("2222222222222222222222", response.getCvu());

        verify(cvuGenerator, times(2)).generate();
    }

    // Duplicate alias
    @Test
    void shouldGenerateAnotherAliasIfAlreadyExists() {

        AccountCreateRequestDto request = new AccountCreateRequestDto();
        request.setUserId(1L);

        when(cvuGenerator.generate()).thenReturn("1234567890123456789012");

        when(aliasGenerator.generate())
                .thenReturn("luna.sol.rio")
                .thenReturn("mar.sur.nube");

        when(accountRepository.existsByCvu(anyString())).thenReturn(false);

        when(accountRepository.existsByAlias("luna.sol.rio")).thenReturn(true);
        when(accountRepository.existsByAlias("mar.sur.nube")).thenReturn(false);

        Account savedAccount = new Account();
        savedAccount.setId(30L);
        savedAccount.setCvu("1234567890123456789012");
        savedAccount.setAlias("mar.sur.nube");

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountCreateResponseDto response = accountService.createAccount(request);

        assertEquals("mar.sur.nube", response.getAlias());

        verify(aliasGenerator, times(2)).generate();
    }
}
