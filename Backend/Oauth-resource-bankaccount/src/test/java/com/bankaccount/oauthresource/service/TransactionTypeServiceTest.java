package com.bankaccount.oauthresource.service;

import com.bankaccount.oauthresource.domain.AccountDomain;
import com.bankaccount.oauthresource.domain.TransactionDomain;
import com.bankaccount.oauthresource.domain.repository.AccountRepository;
import com.bankaccount.oauthresource.domain.repository.TransactionRepository;
import com.bankaccount.oauthresource.domain.service.TransactionTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
class TransactionTypeServiceTest {

    @Autowired
    private TransactionTypeService transactionTypeService;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private AccountRepository accountRepository;

    private TransactionDomain transactionDomain;

    @BeforeEach
    void setUp() {
        AccountDomain accountDomain = AccountDomain.builder()
                .idAccount(87658L)
                .accountName("Random345778")
                .email("user@names.com")
                .currentBalance(new BigDecimal("20000.00"))
                .build();

        Mockito.when(accountRepository.getAccountById(87658L))
                .thenReturn(Optional.of(accountDomain));

        transactionDomain = TransactionDomain.builder()
                .idTransaction(87658L)
                .idAccount(34654363L)
                .transactionAmount(new BigDecimal("10000.45"))
                .transactionTimestamp(LocalDateTime.of(2065, Month.OCTOBER, 12, 13, 12, 0)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        Mockito.when(transactionRepository.saveTransaction(ArgumentMatchers.any())).thenReturn(transactionDomain);
    }

    @Test
    @DisplayName("Should convert one transactionDomain to transactionEntity with DEPOSIT type to repository and return one transactionDomain and add to account's current balance")
    void saveDepositTransaction() {
        Mockito.doNothing().when(accountRepository).updateBalance(new BigDecimal("30000.45"), 87658L);

        accountRepository.updateBalance(new BigDecimal("30000.45"), 87658L);

        TransactionDomain transactionSave = transactionTypeService.saveDepositTransaction(87658L, new BigDecimal("30000.45"));

        assertAll(
                () -> assertEquals(transactionDomain.getIdTransaction(), transactionSave.getIdTransaction()),
                () -> assertEquals(transactionDomain.getIdAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionDomain.getTransactionAmount(), transactionSave.getTransactionAmount()),
                () -> assertEquals(transactionDomain.getTransactionTimestamp(), transactionSave.getTransactionTimestamp()),
                () -> Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(new BigDecimal("30000.45"), 87658L)
        );
    }

    @Test
    @DisplayName("Should convert one transactionDomain to transactionEntity with CHECK type to repository and return one transactionDomain")
    void saveCheckTransaction() {
        TransactionDomain transactionSave = transactionTypeService.saveCheckTransaction(87658L);

        assertAll(
                () -> assertEquals(transactionDomain.getIdTransaction(), transactionSave.getIdTransaction()),
                () -> assertEquals(transactionDomain.getIdAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionDomain.getTransactionAmount(), transactionSave.getTransactionAmount()),
                () -> assertEquals(transactionDomain.getTransactionTimestamp(), transactionSave.getTransactionTimestamp())
        );
    }

    @Test
    @DisplayName("Should convert one transactionDomain to transactionEntity with ONLINE_PAYMENT type to repository and return one transactionDomain and subtract to account's current balance")
    void saveOnlinePaymentTransaction() {
        Mockito.doNothing().when(accountRepository).updateBalance(new BigDecimal("9999.55"), 87658L);

        accountRepository.updateBalance(new BigDecimal("9999.55"), 87658L);

        TransactionDomain transactionSave = transactionTypeService.saveOnlinePaymentTransaction(87658L, new BigDecimal("9999.55"));

        assertAll(
                () -> assertEquals(transactionDomain.getIdTransaction(), transactionSave.getIdTransaction()),
                () -> assertEquals(transactionDomain.getIdAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionDomain.getTransactionAmount(), transactionSave.getTransactionAmount()),
                () -> assertEquals(transactionDomain.getTransactionTimestamp(), transactionSave.getTransactionTimestamp()),
                () -> Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(new BigDecimal("9999.55"), 87658L)
        );
    }

    @Test
    @DisplayName("Should convert one transactionDomain to transactionEntity with WIRE_TRANSFER type to repository and return one transactionDomain and subtract to account's current balance")
    void saveWireTransferTransaction() {
        Mockito.doNothing().when(accountRepository).updateBalance(new BigDecimal("9999.55"), 87658L);

        accountRepository.updateBalance(new BigDecimal("9999.55"), 87658L);

        TransactionDomain transactionSave = transactionTypeService.saveWireTransferTransaction(87658L, new BigDecimal("9999.55"));

        assertAll(
                () -> assertEquals(transactionDomain.getIdTransaction(), transactionSave.getIdTransaction()),
                () -> assertEquals(transactionDomain.getIdAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionDomain.getTransactionAmount(), transactionSave.getTransactionAmount()),
                () -> assertEquals(transactionDomain.getTransactionTimestamp(), transactionSave.getTransactionTimestamp()),
                () -> Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(new BigDecimal("9999.55"), 87658L)
        );
    }
}