package com.bankaccount.back.service;

import com.bankaccount.back.constants.TransactionType;
import com.bankaccount.back.domain.AccountDomain;
import com.bankaccount.back.domain.repository.AccountRepository;
import com.bankaccount.back.domain.repository.TransactionRepository;
import com.bankaccount.back.domain.service.TransactionTypeService;
import com.bankaccount.back.persistence.entity.AccountEntity;
import com.bankaccount.back.persistence.entity.TransactionEntity;
import com.bankaccount.back.web.dto.TransactionDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class TransactionTypeServiceTest {

    @Autowired
    private TransactionTypeService transactionTypeService;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private AccountRepository accountRepository;

    private TransactionDto transactionDto;

    private TransactionDto transactionError;

    TransactionEntity.TransactionEntityBuilder transactionEntity = TransactionEntity.builder();

    @BeforeEach
    void setUp() {
        AccountEntity account = AccountEntity.builder()
                .idAccount(87658)
                .accountName("Random345778")
                .email("user@names.com")
                .password("1234567")
                .currentBalance(new BigDecimal("20000.00"))
                .build();

        AccountEntity accountTransfer = AccountEntity.builder()
                .idAccount(321)
                .accountName("transfer")
                .email("user@names.com")
                .password("1234567")
                .currentBalance(new BigDecimal("20000.00"))
                .build();

        transactionEntity.idTransaction(1L);
        transactionEntity.idAccount(87658);
        transactionEntity.idTransferAccount(321);
        transactionEntity.receiverName("Random345778");
        transactionEntity.transactionAmount(new BigDecimal("10000.45"));
        transactionEntity.transactionTimestamp(LocalDateTime.of(2022, Month.DECEMBER, 11, 13, 12, 0));

        Mockito.when(accountRepository.getAccountById(87658))
                .thenReturn(Optional.of(account));

        Mockito.when(accountRepository.getAccountById(321))
                .thenReturn(Optional.of(account));
    }

    @Test
    @DisplayName("Should throw an exception if the account doesn't have enough money to do the transaction")
    void getBalanceError() throws Exception {
        transactionError = new TransactionDto(
                87658,
                321,
                new BigDecimal("30000.45"),
                TransactionType.ONLINE_PAYMENT);

        Exception exception = assertThrows(Exception.class, () ->
                transactionTypeService.saveTransaction(transactionError));

        String expectedMessage = "Not enough balance";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contentEquals(expectedMessage));
    }

    @Test
    @DisplayName("Should convert one transactionDto to transactionEntity with DEPOSIT type to repository and return one transactionDto and add to account's current balance")
    void saveDepositTransaction() throws Exception {
        transactionDto = new TransactionDto(
                87658,
                321,
                new BigDecimal("10000.45"),
                TransactionType.DEPOSIT);

        transactionEntity.transactionType(TransactionType.DEPOSIT);

        Mockito.when(transactionRepository.saveTransaction(ArgumentMatchers.any())).thenReturn(transactionEntity.build());

        Mockito.doNothing().when(accountRepository).updateBalance(new BigDecimal("30000.45"), 87658);

        TransactionEntity transactionSave = transactionTypeService.saveTransaction(transactionDto);
        System.out.println(transactionSave.toString());

        assertAll(
                () -> assertEquals(transactionDto.idAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionDto.idTransferAccount(), transactionSave.getIdTransferAccount()),
                () -> assertEquals("Random345778", transactionSave.getReceiverName()),
                () -> assertEquals(transactionDto.amount(), transactionSave.getTransactionAmount()),
                () -> assertEquals(transactionDto.transactionType(), transactionSave.getTransactionType()),
                () -> Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(new BigDecimal("30000.45"), 87658)
        );
    }

    @Test
    @DisplayName("Should convert one transactionDto to transactionEntity with ONLINE_PAYMENT type to repository and return one transactionDto and subtract to account's current balance")
    void saveOnlinePaymentTransaction() throws Exception {
        transactionDto = new TransactionDto(
                87658,
                321,
                new BigDecimal("10000.45"),
                TransactionType.ONLINE_PAYMENT);

        transactionEntity.transactionType(TransactionType.ONLINE_PAYMENT);

        Mockito.when(transactionRepository.saveTransaction(ArgumentMatchers.any())).thenReturn(transactionEntity.build());

        Mockito.doNothing().when(accountRepository).updateBalance(new BigDecimal("9999.55"), 87658);

        TransactionEntity transactionSave = transactionTypeService.saveTransaction(transactionDto);

        assertAll(
                () -> assertEquals(transactionDto.idAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionDto.idTransferAccount(), transactionSave.getIdTransferAccount()),
                () -> assertEquals("Random345778", transactionSave.getReceiverName()),
                () -> assertEquals(transactionDto.amount(), transactionSave.getTransactionAmount()),
                () -> assertEquals(transactionDto.transactionType(), transactionSave.getTransactionType()),
                () -> Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(new BigDecimal("9999.55"), 87658)
        );
    }

    @Test
    @DisplayName("Should convert one transactionDto to transactionEntity with WIRE_TRANSFER type to repository and return one transactionDto and subtract to account's current balance")
    void saveWireTransferTransaction() throws Exception {
        transactionDto = new TransactionDto(
                87658,
                321,
                new BigDecimal("10000.45"),
                TransactionType.WIRE_TRANSFER);

        transactionEntity.transactionType(TransactionType.WIRE_TRANSFER);

        Mockito.when(transactionRepository.saveTransaction(ArgumentMatchers.any())).thenReturn(transactionEntity.build());

        Mockito.doNothing().when(accountRepository).updateBalance(new BigDecimal("9999.55"), 87658);

        TransactionEntity transactionSave = transactionTypeService.saveTransaction(transactionDto);

        assertAll(
                () -> assertEquals(transactionDto.idAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionDto.idTransferAccount(), transactionSave.getIdTransferAccount()),
                () -> assertEquals("Random345778", transactionSave.getReceiverName()),
                () -> assertEquals(transactionDto.amount(), transactionSave.getTransactionAmount()),
                () -> assertEquals(transactionDto.transactionType(), transactionSave.getTransactionType()),
                () -> Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(new BigDecimal("9999.55"), 87658)
        );
    }
}
